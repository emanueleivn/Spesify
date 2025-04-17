package com.manele.spesify.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manele.spesify.model.ShoppingList
import com.manele.spesify.repo.ShoppingListRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ShoppingListViewModel(private val repository: ShoppingListRepository) : ViewModel() {

    private val _shoppingLists = MutableStateFlow<List<ShoppingList>>(emptyList())
    val shoppingLists: StateFlow<List<ShoppingList>> = _shoppingLists

    init {
        loadShoppingLists()
    }

    fun loadShoppingLists() {
        viewModelScope.launch {
            _shoppingLists.value = repository.getAllShoppingLists()
        }
    }

    fun addShoppingList(title: String): ShoppingList {
        val newList = ShoppingList(id = System.currentTimeMillis(), title = title)
        viewModelScope.launch {
            repository.addShoppingList(newList)
            loadShoppingLists()
        }
        return newList
    }

    fun deleteShoppingList(id: Long) {
        viewModelScope.launch {
            repository.deleteShoppingList(id)
            loadShoppingLists()
        }
    }
    fun renameShoppingList(id: Long, newTitle: String) {
        viewModelScope.launch {
            val list = repository.getShoppingListById(id)
            if (list != null) {
                val updatedList = list.copy(title = newTitle)
                repository.updateShoppingList(updatedList)
                loadShoppingLists()
            }
        }
    }

    fun duplicateShoppingList(originalList: ShoppingList) {
        viewModelScope.launch {
            val newList = originalList.copy(id = System.currentTimeMillis(), title = "${originalList.title} (Copia)")
            repository.addShoppingList(newList)

            val originalProducts = repository.getShoppingListById(originalList.id)?.products ?: emptyList()
            originalProducts.forEach { product ->
                val newProduct = product.copy(id = System.currentTimeMillis()) // Genera un nuovo ID univoco
                repository.addProductToList(newList.id, newProduct)
            }

            loadShoppingLists()
        }
    }


}
