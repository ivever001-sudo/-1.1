package com.example.accountingapp.ui.screens.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.accountingapp.AccountingApp
import com.example.accountingapp.data.model.Category
import com.example.accountingapp.data.model.Transaction
import com.example.accountingapp.data.model.TransactionType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar

data class HomeUiState(
    val monthIncome: Double = 0.0,
    val monthExpense: Double = 0.0,
    val monthBalance: Double = 0.0,
    val recentTransactions: List<TransactionWithCategory> = emptyList(),
    val currentMonth: Int = 0,
    val currentYear: Int = 0
)

data class TransactionWithCategory(
    val transaction: Transaction,
    val categoryEmoji: String,
    val categoryName: String
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as AccountingApp).repository

    private val currentMonthStart: Long
        get() {
            val cal = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            return cal.timeInMillis
        }

    private val currentMonthEnd: Long
        get() {
            val cal = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }
            return cal.timeInMillis
        }

    private val allCategories: Flow<List<Category>> = repository.getAllCategories()

    private val monthTransactions: Flow<List<Transaction>> =
        repository.getTransactionsByDateRange(currentMonthStart, currentMonthEnd)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<HomeUiState> = combine(
        repository.getTotalByType(TransactionType.INCOME, currentMonthStart, currentMonthEnd),
        repository.getTotalByType(TransactionType.EXPENSE, currentMonthStart, currentMonthEnd),
        repository.getRecentTransactions(10),
        allCategories
    ) { income, expense, transactions, categories ->
        val txWithCat = transactions.map { tx ->
            val cat = categories.find { it.id == tx.categoryId }
            TransactionWithCategory(
                transaction = tx,
                categoryEmoji = cat?.emoji ?: "📌",
                categoryName = cat?.name ?: "未分类"
            )
        }

        val cal = Calendar.getInstance()
        HomeUiState(
            monthIncome = income,
            monthExpense = expense,
            monthBalance = income - expense,
            recentTransactions = txWithCat,
            currentMonth = cal.get(Calendar.MONTH) + 1,
            currentYear = cal.get(Calendar.YEAR)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )
}
