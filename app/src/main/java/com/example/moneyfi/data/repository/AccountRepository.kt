package com.example.moneyfi.data.repository

import com.example.moneyfi.data.dao.AccountDao
import com.example.moneyfi.data.models.Account
import kotlinx.coroutines.flow.Flow

class AccountRepository(private val accountDao: AccountDao) {
    fun getAllAccounts(): Flow<List<Account>> {
        return accountDao.getAllAccounts()
    }

    suspend fun insertAccount(account: Account): Long {
        return accountDao.insert(account)
    }

    suspend fun updateAccount(account: Account) {
        accountDao.update(account)
    }

    suspend fun deleteAccount(account: Account) {
        accountDao.delete(account)
    }
}