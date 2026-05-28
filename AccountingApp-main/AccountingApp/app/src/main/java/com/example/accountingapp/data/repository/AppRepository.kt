package com.example.accountingapp.data.repository

import com.example.accountingapp.data.db.AppDatabase
import com.example.accountingapp.data.db.CategorySummary
import com.example.accountingapp.data.model.Category
import com.example.accountingapp.data.model.Transaction
import com.example.accountingapp.data.model.TransactionType
import kotlinx.coroutines.flow.Flow

class AppRepository(database: AppDatabase) {

    private val transactionDao = database.transactionDao()
    private val categoryDao = database.categoryDao()

    // --- Transactions ---

    fun getAllTransactions(): Flow<List<Transaction>> = transactionDao.getAll()

    fun getRecentTransactions(limit: Int = 20): Flow<List<Transaction>> =
        transactionDao.getRecent(limit)

    fun getTransactionsByDateRange(start: Long, end: Long): Flow<List<Transaction>> =
        transactionDao.getByDateRange(start, end)

    fun getTotalByType(type: TransactionType, start: Long, end: Long): Flow<Double> =
        transactionDao.getTotalByType(type, start, end)

    fun getCategorySummary(type: TransactionType, start: Long, end: Long): Flow<List<CategorySummary>> =
        transactionDao.getCategorySummary(type, start, end)

    suspend fun insertTransaction(transaction: Transaction) =
        transactionDao.insert(transaction)

    suspend fun deleteTransaction(transaction: Transaction) =
        transactionDao.delete(transaction)

    // --- Categories ---

    fun getAllCategories(): Flow<List<Category>> = categoryDao.getAll()

    fun getCategoriesByType(type: TransactionType): Flow<List<Category>> =
        categoryDao.getByType(type)

    suspend fun getCategoryById(id: Long): Category? = categoryDao.getById(id)

    suspend fun insertCategory(category: Category): Long = categoryDao.insert(category)

    suspend fun deleteCategory(category: Category) = categoryDao.delete(category)
}
