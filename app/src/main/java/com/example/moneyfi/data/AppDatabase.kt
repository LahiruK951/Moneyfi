package com.example.moneyfi.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.moneyfi.data.dao.AccountDao
import com.example.moneyfi.data.dao.CategoryDao
import com.example.moneyfi.data.dao.TransactionDao
import com.example.moneyfi.data.models.Account
import com.example.moneyfi.data.models.Category
import com.example.moneyfi.data.models.Transaction
import com.example.moneyfi.utils.Converters

@Database(
    entities = [Transaction::class, Category::class, Account::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun accountDao(): AccountDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "moneyfi_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}