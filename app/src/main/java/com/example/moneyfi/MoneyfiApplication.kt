package com.example.moneyfi

import android.app.Application
import com.example.moneyfi.data.AppDatabase
import com.example.moneyfi.data.repository.AccountRepository
import com.example.moneyfi.data.repository.CategoryRepository
import com.example.moneyfi.data.repository.TransactionRepository

class MoneyfiApplication : Application() {
    lateinit var database: AppDatabase
        private set

    lateinit var transactionRepository: TransactionRepository
        private set

    lateinit var categoryRepository: CategoryRepository
        private set

    lateinit var accountRepository: AccountRepository
        private set

    override fun onCreate() {
        super.onCreate()

        // Initialize database
        database = AppDatabase.getDatabase(this)

        // Initialize repositories
        transactionRepository = TransactionRepository(database.transactionDao())
        categoryRepository = CategoryRepository(database.categoryDao())
        accountRepository = AccountRepository(database.accountDao())

        // Initialize default data if needed
        initializeDefaultData()
    }

    private fun initializeDefaultData() {
        // TODO: Add default categories and accounts if needed
    }
}