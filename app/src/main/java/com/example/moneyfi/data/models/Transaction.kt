package com.example.moneyfi.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

enum class TransactionType {
    INCOME,
    EXPENSE,
    TRANSFER
}

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: TransactionType,
    val amount: Double,
    val description: String,
    val date: Date,
    val categoryId: Long,
    val accountId: Long,
    val toAccountId: Long? = null  // Only used for transfers
)