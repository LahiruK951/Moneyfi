package com.example.moneyfi.ui.transaction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.moneyfi.data.models.Account
import com.example.moneyfi.data.models.Category
import com.example.moneyfi.data.models.Transaction
import com.example.moneyfi.data.models.TransactionType
import com.example.moneyfi.data.repository.AccountRepository
import com.example.moneyfi.data.repository.CategoryRepository
import com.example.moneyfi.data.repository.TransactionRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Date

class AddTransactionViewModel(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _accounts = MutableLiveData<List<Account>>()
    val accounts: LiveData<List<Account>> = _accounts

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories

    private val _saveComplete = MutableLiveData<Boolean>()
    val saveComplete: LiveData<Boolean> = _saveComplete

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private var selectedType = TransactionType.EXPENSE
    private var selectedDate = Date()
    private var cachedAccounts: List<Account> = emptyList()
    private var cachedCategories: List<Category> = emptyList()

    init {
        loadAccounts()
        loadCategories()
    }

    private fun loadAccounts() {
        viewModelScope.launch {
            accountRepository.getAllAccounts().collect { accounts ->
                cachedAccounts = accounts
                _accounts.value = accounts
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            categoryRepository.getCategoriesByType(TransactionType.EXPENSE).collect { categories ->
                cachedCategories = categories
                _categories.value = categories
            }
        }
    }

    fun onTransactionTypeSelected(type: TransactionType) {
        selectedType = type
        viewModelScope.launch {
            if (type != TransactionType.TRANSFER) {
                categoryRepository.getCategoriesByType(type).first().let {
                    cachedCategories = it
                    _categories.value = it
                }
            }
        }
    }

    fun onDateSelected(date: Date) {
        selectedDate = date
    }

    fun saveTransaction(
        amount: Double,
        description: String,
        fromAccountPosition: Int,
        toAccountPosition: Int,
        categoryPosition: Int
    ) {
        if (cachedAccounts.isEmpty()) {
            _error.value = "No accounts available"
            return
        }

        if (selectedType != TransactionType.TRANSFER && cachedCategories.isEmpty()) {
            _error.value = "No categories available"
            return
        }

        viewModelScope.launch {
            try {
                val fromAccount = cachedAccounts[fromAccountPosition]

                val transaction = Transaction(
                    type = selectedType,
                    amount = amount,
                    description = description,
                    date = selectedDate,
                    categoryId = if (selectedType == TransactionType.TRANSFER) 0 else cachedCategories[categoryPosition].id,
                    accountId = fromAccount.id,
                    toAccountId = if (selectedType == TransactionType.TRANSFER)
                        cachedAccounts[toAccountPosition].id else null
                )

                // Update account balances
                when (selectedType) {
                    TransactionType.EXPENSE -> {
                        fromAccount.balance -= amount
                    }
                    TransactionType.INCOME -> {
                        fromAccount.balance += amount
                    }
                    TransactionType.TRANSFER -> {
                        fromAccount.balance -= amount
                        val toAccount = cachedAccounts[toAccountPosition]
                        toAccount.balance += amount
                        accountRepository.updateAccount(toAccount)
                    }
                }

                accountRepository.updateAccount(fromAccount)
                transactionRepository.insertTransaction(transaction)
                _saveComplete.value = true

            } catch (e: Exception) {
                _error.value = "Failed to save transaction: ${e.message}"
            }
        }
    }

    class Factory(
        private val transactionRepository: TransactionRepository,
        private val accountRepository: AccountRepository,
        private val categoryRepository: CategoryRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AddTransactionViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AddTransactionViewModel(
                    transactionRepository,
                    accountRepository,
                    categoryRepository
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}