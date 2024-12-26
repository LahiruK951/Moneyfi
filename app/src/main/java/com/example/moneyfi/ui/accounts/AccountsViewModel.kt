package com.example.moneyfi.ui.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.moneyfi.data.models.Account
import com.example.moneyfi.data.repository.AccountRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AccountsViewModel(
    private val accountRepository: AccountRepository
) : ViewModel() {

    val accounts = accountRepository.getAllAccounts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addAccount(name: String, initialBalance: Double) {
        if (name.isBlank()) return

        viewModelScope.launch {
            val account = Account(
                name = name,
                balance = initialBalance
            )
            accountRepository.insertAccount(account)
        }
    }

    fun updateAccount(account: Account) {
        if (account.name.isBlank()) return

        viewModelScope.launch {
            accountRepository.updateAccount(account)
        }
    }

    fun deleteAccount(account: Account) {
        viewModelScope.launch {
            accountRepository.deleteAccount(account)
        }
    }

    class Factory(private val repository: AccountRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AccountsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AccountsViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}