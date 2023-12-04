package com.example.mad.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shoppingListTable")
data class ShoppingListItem (

    @ColumnInfo(name = "amount")
    val amount: Int,

    @ColumnInfo(name = "shoppingListItem")
    val product: String,

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0
)
