package com.manele.spesify.presentation

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.manele.spesify.R
import com.manele.spesify.model.Product
import com.manele.spesify.repo.ShoppingListRepository
import kotlin.math.roundToInt
import androidx.compose.foundation.gestures.Orientation
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListDetailScreen(
    navController: NavController,
    listId: Long,
    repository: ShoppingListRepository,
) {
    val viewModel: ProductViewModel =
        viewModel(factory = ProductViewModelFactory(repository, listId))
    val products by viewModel.products.collectAsState()
    val editingProduct by viewModel.editingProduct.collectAsState()
    var shoppingListTitle by remember { mutableStateOf("Lista della Spesa") }
    val showAddDialog by viewModel.showAddProductDialog.collectAsState()
    var productToDelete by remember { mutableStateOf<Product?>(null) }
    var bottomSheetProduct by remember { mutableStateOf<Product?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Colori disponibili
    val colors = listOf(
        Color(ContextCompat.getColor(LocalContext.current,R.color.colorPrimary)),
        MaterialTheme.colorScheme.secondaryContainer,
        Color(0xFFD2C9EE)
    )
    var cardColor by remember { mutableStateOf(colors[0]) }

    // Recupera titolo e colore della lista
    LaunchedEffect(listId) {
        val shoppingList = repository.getShoppingListById(listId)
        shoppingListTitle = shoppingList?.title ?: "Lista della Spesa"

        val allLists = repository.getAllShoppingLists()
        val listIndex = allLists.indexOfFirst { it.id == listId }.takeIf { it >= 0 } ?: 0
        cardColor = colors[listIndex % colors.size]
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TopAppBar(
                    title = {
                        Text(
                            text = shoppingListTitle,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Torna indietro")
                        }
                    }
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.products),
                        contentDescription = "Background Image",
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(0.6f),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                TotalPrice(products)
                Spacer(modifier = Modifier.height(4.dp))
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.showAddProductDialog() }) {
                Icon(Icons.Default.Add, contentDescription = "Aggiungi")
            }
        }
    ) { padding ->
        val sortedProducts = remember(products) {
            products.sortedWith(compareBy<Product> { it.isPurchased }.thenBy { it.id })
        }

        Column(modifier = Modifier.padding(padding)) {
            if (sortedProducts.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Nessun prodotto inserito", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .animateContentSize()
                ) {
                    items(
                        items = sortedProducts,
                        key = { it.id }
                    ) { product ->
                        ProductItem(
                            product = product,
                            cardColor = cardColor,
                            onOptionsClick = { bottomSheetProduct = product },
                            onSwipeRight = { viewModel.markAsPurchased(product) },
                            onSwipeLeft = { viewModel.restoreProduct(product) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddProductDialog(
            onDismiss = { viewModel.dismissAddProductDialog() },
            onSave = { name, quantity, price ->
                viewModel.addProduct(name, quantity, price)
            }
        )
    }

    if (editingProduct != null) {
        EditProductDialog(
            product = editingProduct!!,
            onDismiss = { viewModel.dismissEditProductDialog() },
            onSave = { updatedProduct ->
                viewModel.updateProduct(updatedProduct)
            }
        )
    }

    if (productToDelete != null) {
        AlertDialog(
            onDismissRequest = { productToDelete = null },
            title = { Text("Conferma eliminazione") },
            text = { Text("Sei sicuro di voler eliminare \"${productToDelete!!.name}\"?") },
            confirmButton = {
                Button(onClick = {
                    viewModel.deleteProduct(productToDelete!!.id)
                    productToDelete = null
                }) {
                    Text("Elimina")
                }
            },
            dismissButton = {
                Button(
                    onClick = { productToDelete = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                ) {
                    Text("Annulla")
                }
            },
            titleContentColor = Color.Black,
            textContentColor = Color.Black
        )
    }

    bottomSheetProduct?.let { product ->
        ModalBottomSheet(
            onDismissRequest = { bottomSheetProduct = null },
            sheetState = sheetState
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Opzioni per \"${product.name}\"", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        viewModel.showEditProductDialog(product)
                        bottomSheetProduct = null
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Modifica")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        productToDelete = product
                        bottomSheetProduct = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Elimina")
                }
            }
        }
    }
}

@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun ProductItem(
    product: Product,
    cardColor: Color,
    onOptionsClick: () -> Unit,
    onSwipeRight: () -> Unit = {},
    onSwipeLeft: () -> Unit = {}
) {
    val isCompleted = product.isPurchased
    val context = LocalContext.current

    val swipeableState = rememberSwipeableState(initialValue = 0)
    val swipeThreshold = 150f
    val anchors = mapOf(
        0f to 0,
        swipeThreshold to 1,
        -swipeThreshold to -1
    )

    LaunchedEffect(swipeableState.currentValue) {
        when (swipeableState.currentValue) {
            1 -> {
                onSwipeRight()
                Toast.makeText(context, "Acquistato", Toast.LENGTH_SHORT).show()
                swipeableState.snapTo(0)
            }
            -1 -> {
                onSwipeLeft()
                Toast.makeText(context, "Ripristinato", Toast.LENGTH_SHORT).show()
                swipeableState.snapTo(0)
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .swipeable(
                state = swipeableState,
                anchors = anchors,
                thresholds = { _, _ -> FractionalThreshold(0.3f) },
                orientation = Orientation.Horizontal
            ),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .offset { IntOffset(swipeableState.offset.value.roundToInt(), 0) }
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                        color = Color.Black
                    )
                )
                Text(
                    text = "Quantità: ${product.quantity} | Costo unità: ${product.unitPrice}€",
                    color = Color.Black
                )
            }
            IconButton(
                onClick = onOptionsClick
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Opzioni",
                    tint = Color.Black
                )
            }
        }
    }
}

@Composable
fun TotalPrice(products: List<Product>) {
    val total = products.sumOf { it.totalPrice() }
    Text(
        text = "Totale complessivo: €${"%.2f".format(total)}",
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(bottom = 8.dp),
        textAlign = TextAlign.Center
    )
}

@Composable
fun AddProductDialog(
    onDismiss: () -> Unit,
    onSave: (String, Int, Double) -> Unit
) {
    var productName by remember { mutableStateOf("") }
    var productQuantity by remember { mutableStateOf("1") }
    var productPrice by remember { mutableStateOf("0.0") }
    var productError by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Aggiungi Prodotto") },
        text = {
            Column {
                OutlinedTextField(
                    value = productName,
                    onValueChange = {
                        productName = it
                        if (it.isNotBlank()) productError = ""
                    },
                    label = { Text("Nome prodotto") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        capitalization = KeyboardCapitalization.Sentences
                    )
                )
                if (productError.isNotEmpty()) {
                    Text(productError, color = MaterialTheme.colorScheme.error)
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = productQuantity,
                    onValueChange = {
                        if (it.all { c -> c.isDigit() }) productQuantity = it
                    },
                    label = { Text("Quantità") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = productPrice,
                    onValueChange = {
                        if (it.matches(Regex("^\\d*(\\.\\d{0,2})?$"))) {
                            productPrice = it
                        }
                    },
                    label = { Text("Prezzo unitario") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal)
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                if (productName.isBlank()) {
                    productError = "inserire nome"
                } else {
                    val qty = productQuantity.toIntOrNull() ?: 1
                    val price = productPrice.toDoubleOrNull() ?: 0.0
                    onSave(productName, qty, price)
                    onDismiss()
                }
            }) {
                Text("Salva")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
            ) {
                Text("Annulla")
            }
        }
    )
}

@Composable
fun EditProductDialog(
    product: Product,
    onDismiss: () -> Unit,
    onSave: (Product) -> Unit,
) {
    var productName by remember { mutableStateOf(product.name) }
    var productQuantity by remember { mutableStateOf(product.quantity.toString()) }
    var productPrice by remember { mutableStateOf(product.unitPrice.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Modifica Prodotto") },
        text = {
            Column {
                OutlinedTextField(
                    value = productName,
                    onValueChange = { productName = it },
                    label = { Text("Nome prodotto") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        capitalization = KeyboardCapitalization.Sentences
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = productQuantity,
                    onValueChange = {
                        if (it.all { c -> c.isDigit() }) {
                            productQuantity = it
                        }
                    },
                    label = { Text("Quantità") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = productPrice,
                    onValueChange = {
                        if (it.matches(Regex("^\\d*(\\.\\d{0,2})?$"))) {
                            productPrice = it
                        }
                    },
                    label = { Text("Prezzo unitario") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal)
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val qty = productQuantity.toIntOrNull() ?: 1
                val price = productPrice.toDoubleOrNull() ?: 0.0
                val updated = product.copy(
                    name = productName,
                    quantity = qty,
                    unitPrice = price
                )
                onSave(updated)
                onDismiss()
            }) {
                Text("Salva")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
            ) {
                Text("Annulla")
            }
        }
    )
}
