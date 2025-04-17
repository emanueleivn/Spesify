package com.manele.spesify.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manele.spesify.model.Product
import com.manele.spesify.repo.ShoppingListRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductViewModel(
    private val repository: ShoppingListRepository,
    private val listId: Long
) : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    private val _showAddProductDialog = MutableStateFlow(false)
    val showAddProductDialog: StateFlow<Boolean> = _showAddProductDialog

    private val _editingProduct = MutableStateFlow<Product?>(null)
    val editingProduct: StateFlow<Product?> = _editingProduct

    init {
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch {
            val shoppingList = repository.getShoppingListById(listId)
            _products.value = shoppingList?.products?.sortedBy { it.isPurchased } ?: emptyList()
        }
    }

    fun addProduct(name: String, quantity: Int, price: Double) {
        viewModelScope.launch {
            if (name.isBlank() || quantity <= 0 || price < 0) return@launch

            val newProduct = Product(
                id = System.currentTimeMillis(),
                name = name,
                quantity = quantity,
                unitPrice = price,
                isPurchased = false
            )

            repository.addProductToList(listId, newProduct)
            loadProducts()
            _showAddProductDialog.value = false
        }
    }

    fun showAddProductDialog() {
        _showAddProductDialog.value = true
    }

    fun dismissAddProductDialog() {
        _showAddProductDialog.value = false
    }

    fun markAsPurchased(product: Product) {
        viewModelScope.launch {
            val updatedProduct = product.copy(isPurchased = true)
            repository.updateProductInList(listId, updatedProduct)
            loadProducts()
        }
    }

    fun restoreProduct(product: Product) {
        viewModelScope.launch {
            val updatedProduct = product.copy(isPurchased = false)
            repository.updateProductInList(listId, updatedProduct)
            loadProducts()
        }
    }

    fun deleteProduct(productId: Long) {
        viewModelScope.launch {
            repository.removeProductFromList(listId, productId)
            loadProducts()
        }
    }

    fun showEditProductDialog(product: Product) {
        _editingProduct.value = product
    }

    fun dismissEditProductDialog() {
        _editingProduct.value = null
    }

    fun updateProduct(updatedProduct: Product) {
        viewModelScope.launch {
            repository.updateProductInList(listId, updatedProduct)
            loadProducts()
            dismissEditProductDialog()
        }
    }
}
