package com.example.moneyfi.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.moneyfi.data.models.Transaction
import com.example.moneyfi.data.models.TransactionType
import com.example.moneyfi.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class HomeViewModel(private val transactionRepository: TransactionRepository) : ViewModel() {
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions

    private val _startDate = MutableStateFlow<Date>(getStartOfMonth())
    val startDate: StateFlow<Date> = _startDate

    private val _endDate = MutableStateFlow<Date>(getEndOfMonth())
    val endDate: StateFlow<Date> = _endDate

    private val _totalIncome = MutableStateFlow(0.0)
    val totalIncome: StateFlow<Double> = _totalIncome

    private val _totalExpense = MutableStateFlow(0.0)
    val totalExpense: StateFlow<Double> = _totalExpense

    init {
        loadTransactions()
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            transactionRepository.getTransactionsByDateRange(startDate.value, endDate.value)
                .collect { transactions ->
                    _transactions.value = transactions
                    calculateTotals(transactions)
                }
        }
    }

    private fun calculateTotals(transactions: List<Transaction>) {
        var income = 0.0
        var expense = 0.0

        transactions.forEach { transaction ->
            when (transaction.type) {
                TransactionType.INCOME -> income += transaction.amount
                TransactionType.EXPENSE -> expense += transaction.amount
                TransactionType.TRANSFER -> {} // Transfers don't affect totals
            }
        }

        _totalIncome.value = income
        _totalExpense.value = expense
    }

    fun updateDateRange(start: Date, end: Date) {
        _startDate.value = start
        _endDate.value = end
        loadTransactions()
    }

    private fun getStartOfMonth(): Date {
        return Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
    }

    private fun getEndOfMonth(): Date {
        return Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.time
    }

    class Factory(private val repository: TransactionRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}