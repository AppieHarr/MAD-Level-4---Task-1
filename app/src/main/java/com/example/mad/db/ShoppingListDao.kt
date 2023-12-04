package com.example.mad.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.mad.data.ShoppingListItem

@Dao
interface ShoppingListDao {

    @Query("SELECT * FROM shoppingListTable")
    fun getAllShoppingListItems(): LiveData<List<ShoppingListItem>>

    @Insert
    suspend fun insertShoppingListItem(shoppingListItem: ShoppingListItem)

    @Delete
    suspend fun deleteShoppingListItem(shoppingListItem: ShoppingListItem)

    @Query("DELETE FROM shoppingListTable")
    suspend fun deleteAllShoppingListItems()

    @Update
    suspend fun updateShoppingListItem(shoppingListItem: ShoppingListItem)

}