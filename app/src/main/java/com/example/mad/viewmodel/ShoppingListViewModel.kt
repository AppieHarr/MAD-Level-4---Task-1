package com.example.mad.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.mad.data.ShoppingListItem
import com.example.mad.repository.ShoppingListRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShoppingListViewModel (application: Application) : AndroidViewModel(application) {

    private val ioScope = CoroutineScope(Dispatchers.IO)
    private val shoppingListRepository = ShoppingListRepository(application.applicationContext)

    val shoppingListItems: LiveData<List<ShoppingListItem>> = shoppingListRepository.getAllShoppingListItems()

    fun insertShoppingListItem(shoppingListItem: ShoppingListItem) {
        ioScope.launch {
            shoppingListRepository.insertShoppingListItem(shoppingListItem)
        }
    }

    fun deleteShoppingListItem(shoppingListItem: ShoppingListItem) {
        ioScope.launch {
            shoppingListRepository.deleteShoppingListItem(shoppingListItem)
        }
    }

    fun deleteAllShoppingListItems() {
        ioScope.launch {
            shoppingListRepository.deleteAllShoppingListItems()
        }
    }

    fun updateShoppingListItem(shoppingListItem: ShoppingListItem) {
        ioScope.launch {
            shoppingListRepository.updateShoppingListItem(shoppingListItem)
        }
    }
}