package com.manele.spesify.presentation

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.manele.spesify.R
import com.manele.spesify.application.Screen
import com.manele.spesify.model.ShoppingList
import com.manele.spesify.repo.ShoppingListRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListsScreen(
    navController: NavController,
    repository: ShoppingListRepository
) {
    val viewModel: ShoppingListViewModel =
        viewModel(factory = ShoppingListViewModelFactory(repository))
    val shoppingLists by viewModel.shoppingLists.collectAsState()

    var showNewListDialog by remember { mutableStateOf(false) }
    var newListName by remember { mutableStateOf("") }
    var newListError by remember { mutableStateOf("") }

    var bottomSheetShoppingList by remember { mutableStateOf<ShoppingList?>(null) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var listForRename by remember { mutableStateOf<ShoppingList?>(null) }
    var renameText by remember { mutableStateOf("") }
    var shoppingListToDelete by remember { mutableStateOf<ShoppingList?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(navController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.route == Screen.ShoppingLists.route) {
                viewModel.loadShoppingLists()
            }
        }
    }

    Scaffold(
        topBar = {
            Box {
                Image(
                    painter = painterResource(id = R.drawable.listscreen),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                        .alpha(0.9f)
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    MaterialTheme.colorScheme.background
                                )
                            )
                        )
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {

                    Text(
                        text = "Organizza le tue liste della spesa",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showNewListDialog = true
                    newListError = ""
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Aggiungi lista")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (shoppingLists.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "Nessuna lista della spesa trovata",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    itemsIndexed(shoppingLists) { index, list ->
                        ShoppingListItem(
                            list = list,
                            index = index,
                            navController = navController,
                            viewModel = viewModel,
                            onLongPress = { bottomSheetShoppingList = it }
                        )
                    }

                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }

    if (bottomSheetShoppingList != null) {
        ModalBottomSheet(
            onDismissRequest = { bottomSheetShoppingList = null },
            sheetState = sheetState
        ) {
            bottomSheetShoppingList?.let { list ->
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Opzioni per \"${list.title}\"",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            listForRename = list
                            renameText = list.title
                            bottomSheetShoppingList = null
                            showRenameDialog = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Rinomina")
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            viewModel.duplicateShoppingList(list)
                            bottomSheetShoppingList = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Duplica")
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            viewModel.clearProductsFromList(list.id)
                            bottomSheetShoppingList = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Svuota lista")
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            shoppingListToDelete = list
                            bottomSheetShoppingList = null
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

    if (showRenameDialog && listForRename != null) {
        AlertDialog(
            onDismissRequest = {
                showRenameDialog = false
                listForRename = null
            },
            title = { Text("Rinomina Lista") },
            text = {
                OutlinedTextField(
                    value = renameText,
                    onValueChange = { renameText = it },
                    label = { Text("Nome della lista") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        capitalization = KeyboardCapitalization.Sentences
                    )
                )
            },
            confirmButton = {
                Button(onClick = {
                    listForRename?.let { list ->
                        viewModel.renameShoppingList(list.id, renameText)
                    }
                    showRenameDialog = false
                    listForRename = null
                }) {
                    Text("Conferma")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showRenameDialog = false
                        listForRename = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                ) {
                    Text("Annulla")
                }
            },
            titleContentColor = Color.Black,
            textContentColor = Color.Black
        )
    }

    if (shoppingListToDelete != null) {
        AlertDialog(
            onDismissRequest = { shoppingListToDelete = null },
            title = { Text("Conferma eliminazione") },
            text = { Text("Sei sicuro di voler eliminare \"${shoppingListToDelete!!.title}\"?") },
            confirmButton = {
                Button(onClick = {
                    viewModel.deleteShoppingList(shoppingListToDelete!!.id)
                    shoppingListToDelete = null
                }) {
                    Text("Elimina")
                }
            },
            dismissButton = {
                Button(onClick = { shoppingListToDelete = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)) {
                    Text("Annulla")
                }
            },
            titleContentColor = Color.Black,
            textContentColor = Color.Black
        )
    }

    if (showNewListDialog) {
        AlertDialog(
            onDismissRequest = {
                showNewListDialog = false
                newListError = ""
            },
            title = { Text("Nuova Lista") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newListName,
                        onValueChange = {
                            newListName = it
                            if (it.isNotBlank()) newListError = ""
                        },
                        label = { Text("Nome della lista") },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            capitalization = KeyboardCapitalization.Sentences
                        )
                    )
                    if (newListError.isNotEmpty()) {
                        Text(newListError, color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (newListName.isBlank()) {
                        newListError = "inserire nome"
                    } else {
                        val newList = viewModel.addShoppingList(newListName)
                        showNewListDialog = false
                        newListName = ""
                        newListError = ""
                        navController.navigate(Screen.ShoppingListDetail.createRoute(newList.id))
                    }
                }) {
                    Text("Conferma")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showNewListDialog = false
                        newListError = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray
                    )
                ) {
                    Text("Annulla")
                }
            },
            titleContentColor = Color.Black,
            textContentColor = Color.Black
        )
    }
}

@Composable
fun ShoppingListItem(
    list: ShoppingList,
    index: Int,
    navController: NavController,
    viewModel: ShoppingListViewModel,
    onLongPress: (ShoppingList) -> Unit
) {
    val colors = listOf(
        Color(ContextCompat.getColor(LocalContext.current,R.color.colorPrimary)),
        MaterialTheme.colorScheme.secondaryContainer,
        Color(0xFFD2C9EE)
    )
    val colorIndex = index % colors.size
    val backgroundColor by animateColorAsState(
        targetValue = colors[colorIndex],
        label = "CardBackground"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable { navController.navigate(Screen.ShoppingListDetail.createRoute(list.id)) },
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text("🛒", fontSize = MaterialTheme.typography.titleMedium.fontSize)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = list.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.Black)
                )
                Text(
                    text = "Totale: €${"%.2f".format(list.total)}",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black)
                )
            }
            IconButton(
                onClick = { onLongPress(list) }
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
