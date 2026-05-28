package com.example.accountingapp.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.accountingapp.data.model.Category
import com.example.accountingapp.data.model.TransactionType
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories ORDER BY id ASC")
    fun getAll(): Flow<List<Category>>

    @Query("SELECT * FROM categories ORDER BY id ASC")
    suspend fun getAllOnce(): List<Category>

    @Query("SELECT * FROM categories WHERE type = :type ORDER BY id ASC")
    fun getByType(type: TransactionType): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getById(id: Long): Category?

    @Insert
    suspend fun insert(category: Category): Long

    @Delete
    suspend fun delete(category: Category)
}
