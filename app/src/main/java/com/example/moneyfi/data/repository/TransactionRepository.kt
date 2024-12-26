package com.example.moneyfi.data.repository

import com.example.moneyfi.data.dao.TransactionDao
import com.example.moneyfi.data.models.Transaction
import java.util.Date
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val transactionDao: TransactionDao) {
    fun getTransactionsByDateRange(startDate: Date, endDate: Date): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByDateRange(startDate, endDate)
    }

    suspend fun insertTransaction(transaction: Transaction): Long {
        return transactionDao.insert(transaction)
    }

    suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.update(transaction)
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.delete(transaction)
    }
}