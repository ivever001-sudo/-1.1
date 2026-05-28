package com.example.accountingapp.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.accountingapp.data.model.Transaction
import com.example.accountingapp.data.model.TransactionType
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAll(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE date BETWEEN :start AND :end ORDER BY date DESC")
    fun getByDateRange(start: Long, end: Long): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions ORDER BY date DESC LIMIT :limit")
    fun getRecent(limit: Int = 20): Flow<List<Transaction>>

    @Query(
        """
        SELECT COALESCE(SUM(amount), 0) FROM transactions
        WHERE type = :type AND date BETWEEN :start AND :end
        """
    )
    fun getTotalByType(type: TransactionType, start: Long, end: Long): Flow<Double>

    @Query(
        """
        SELECT categoryId, COALESCE(SUM(amount), 0) as total
        FROM transactions
        WHERE type = :type AND date BETWEEN :start AND :end
        GROUP BY categoryId
        """
    )
    fun getCategorySummary(type: TransactionType, start: Long, end: Long): Flow<List<CategorySummary>>

    @Insert
    suspend fun insert(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)
}

data class CategorySummary(
    val categoryId: Long,
    val total: Double
)
