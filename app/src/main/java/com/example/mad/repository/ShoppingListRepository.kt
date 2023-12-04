package com.example.mad.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.mad.data.ShoppingListItem
import com.example.mad.db.ShoppingListDao
import com.example.mad.db.ShoppingListDatabase

class ShoppingListRepository(context: Context) {

    private var shoppingListDao: ShoppingListDao

    init {
        val shoppingListDatabase = ShoppingListDatabase.getDatabase(context)
        shoppingListDao = shoppingListDatabase!!.shoppingListDao()
    }

    fun getAllShoppingListItems(): LiveData<List<ShoppingListItem>> {
        return shoppingListDao.getAllShoppingListItems()
    }

    suspend fun insertShoppingListItem(shoppingListItem: ShoppingListItem) {
        shoppingListDao.insertShoppingListItem(shoppingListItem)
    }

    suspend fun deleteShoppingListItem(shoppingListItem: ShoppingListItem) {
        shoppingListDao.deleteShoppingListItem(shoppingListItem)
    }

    suspend fun deleteAllShoppingListItems() {
        shoppingListDao.deleteAllShoppingListItems()
    }

    suspend fun updateShoppingListItem(shoppingListItem: ShoppingListItem) {
        shoppingListDao.updateShoppingListItem(shoppingListItem)
    }

}
