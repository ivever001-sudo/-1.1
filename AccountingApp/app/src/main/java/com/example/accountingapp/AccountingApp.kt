package com.example.accountingapp

import android.app.Application
import com.example.accountingapp.data.db.AppDatabase
import com.example.accountingapp.data.model.Category
import com.example.accountingapp.data.model.TransactionType
import com.example.accountingapp.data.repository.AppRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AccountingApp : Application() {

    val database by lazy { AppDatabase.getInstance(this) }
    val repository by lazy { AppRepository(database) }

    override fun onCreate() {
        super.onCreate()
        seedDefaultCategories()
    }

    private fun seedDefaultCategories() {
        CoroutineScope(Dispatchers.IO).launch {
            val existing = database.categoryDao().getAllOnce()
            if (existing.isNotEmpty()) return@launch

            defaultIncomeCategories.forEach { database.categoryDao().insert(it) }
            defaultExpenseCategories.forEach { database.categoryDao().insert(it) }
        }
    }

    companion object {
        val defaultIncomeCategories = listOf(
            Category(name = "工资", emoji = "💰", type = TransactionType.INCOME),
            Category(name = "奖金", emoji = "🎁", type = TransactionType.INCOME),
            Category(name = "兼职", emoji = "💼", type = TransactionType.INCOME),
            Category(name = "投资", emoji = "📈", type = TransactionType.INCOME),
            Category(name = "其他", emoji = "📦", type = TransactionType.INCOME)
        )

        val defaultExpenseCategories = listOf(
            Category(name = "餐饮", emoji = "🍜", type = TransactionType.EXPENSE),
            Category(name = "交通", emoji = "🚗", type = TransactionType.EXPENSE),
            Category(name = "购物", emoji = "🛍️", type = TransactionType.EXPENSE),
            Category(name = "住房", emoji = "🏠", type = TransactionType.EXPENSE),
            Category(name = "娱乐", emoji = "🎮", type = TransactionType.EXPENSE),
            Category(name = "医疗", emoji = "💊", type = TransactionType.EXPENSE),
            Category(name = "教育", emoji = "📚", type = TransactionType.EXPENSE),
            Category(name = "其他", emoji = "💸", type = TransactionType.EXPENSE)
        )
    }
}
