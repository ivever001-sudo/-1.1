package com.example.accountingapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val type: TransactionType,
    val categoryId: Long,
    val note: String = "",
    val date: Long = System.currentTimeMillis()
)

enum class TransactionType {
    INCOME,
    EXPENSE
}
