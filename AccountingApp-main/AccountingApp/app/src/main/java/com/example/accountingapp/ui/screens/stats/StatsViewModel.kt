package com.example.accountingapp.ui.screens.stats

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.accountingapp.AccountingApp
import com.example.accountingapp.data.db.CategorySummary
import com.example.accountingapp.data.model.Category
import com.example.accountingapp.data.model.TransactionType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class TimeRange { DAY, WEEK, MONTH }

data class CategoryStat(
    val category: Category,
    val total: Double,
    val percentage: Float
)

data class StatsUiState(
    val timeRange: TimeRange = TimeRange.MONTH,
    val year: Int = 2024,
    val month: Int = 1,
    val day: Int = 1,
    val weekStart: Long = 0L,
    val weekEnd: Long = 0L,
    val weekLabel: String = "",
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val expenseCategories: List<CategoryStat> = emptyList(),
    val incomeCategories: List<CategoryStat> = emptyList()
)

class StatsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as AccountingApp).repository

    private val now = Calendar.getInstance()
    private val _timeRange = MutableStateFlow(TimeRange.MONTH)
    private val _year = MutableStateFlow(now.get(Calendar.YEAR))
    private val _month = MutableStateFlow(now.get(Calendar.MONTH) + 1)
    private val _day = MutableStateFlow(now.get(Calendar.DAY_OF_MONTH))
    private val _weekOffset = MutableStateFlow(0)

    private fun getDayRange(year: Int, month: Int, day: Int): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        cal.set(year, month - 1, day, 0, 0, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis
        cal.set(year, month - 1, day, 23, 59, 59)
        cal.set(Calendar.MILLISECOND, 999)
        val end = cal.timeInMillis
        return start to end
    }

    private fun getWeekRange(): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        cal.firstDayOfWeek = Calendar.MONDAY
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        cal.add(Calendar.WEEK_OF_YEAR, _weekOffset.value)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis
        cal.add(Calendar.DAY_OF_WEEK, 6)
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        val end = cal.timeInMillis
        return start to end
    }

    private fun getMonthRange(year: Int, month: Int): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        cal.set(year, month - 1, 1, 0, 0, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis
        cal.set(year, month - 1, cal.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59)
        cal.set(Calendar.MILLISECOND, 999)
        val end = cal.timeInMillis
        return start to end
    }

    private fun formatWeekLabel(startMs: Long, endMs: Long): String {
        val fmt = SimpleDateFormat("MM/dd", Locale.getDefault())
        return "${fmt.format(Date(startMs))} - ${fmt.format(Date(endMs))}"
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<StatsUiState> = combine(
        _timeRange, _year, _month, _day, _weekOffset
    ) { range, year, month, day, weekOffset ->
        range to Triple(year, month, day)
    }.flatMapLatest { (range, date) ->
        val (year, month, day) = date
        val (start, end) = when (range) {
            TimeRange.DAY -> getDayRange(year, month, day)
            TimeRange.WEEK -> getWeekRange()
            TimeRange.MONTH -> getMonthRange(year, month)
        }
        val weekLabel = if (range == TimeRange.WEEK) {
            val (ws, we) = getWeekRange()
            formatWeekLabel(ws, we)
        } else ""

        val (ws, we) = if (range == TimeRange.WEEK) getWeekRange() else 0L to 0L

        combine(
            repository.getTotalByType(TransactionType.INCOME, start, end),
            repository.getTotalByType(TransactionType.EXPENSE, start, end),
            repository.getCategorySummary(TransactionType.EXPENSE, start, end),
            repository.getCategorySummary(TransactionType.INCOME, start, end),
            repository.getAllCategories()
        ) { income, expense, expenseSummary, incomeSummary, categories ->
            StatsUiState(
                timeRange = range,
                year = year,
                month = month,
                day = day,
                weekStart = ws,
                weekEnd = we,
                weekLabel = weekLabel,
                totalIncome = income,
                totalExpense = expense,
                expenseCategories = buildCategoryStats(expenseSummary, categories, expense),
                incomeCategories = buildCategoryStats(incomeSummary, categories, income)
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), StatsUiState())

    // --- Time range switching ---
    fun setTimeRange(range: TimeRange) {
        _timeRange.value = range
    }

    // --- Day navigation ---
    fun previousDay() {
        val cal = Calendar.getInstance()
        cal.set(_year.value, _month.value - 1, _day.value)
        cal.add(Calendar.DAY_OF_MONTH, -1)
        _year.value = cal.get(Calendar.YEAR)
        _month.value = cal.get(Calendar.MONTH) + 1
        _day.value = cal.get(Calendar.DAY_OF_MONTH)
    }

    fun nextDay() {
        val cal = Calendar.getInstance()
        cal.set(_year.value, _month.value - 1, _day.value)
        cal.add(Calendar.DAY_OF_MONTH, 1)
        _year.value = cal.get(Calendar.YEAR)
        _month.value = cal.get(Calendar.MONTH) + 1
        _day.value = cal.get(Calendar.DAY_OF_MONTH)
    }

    // --- Week navigation ---
    fun previousWeek() {
        _weekOffset.value -= 1
    }

    fun nextWeek() {
        _weekOffset.value += 1
    }

    // --- Month navigation ---
    fun previousMonth() {
        if (_month.value == 1) {
            _month.value = 12
            _year.value -= 1
        } else {
            _month.value -= 1
        }
    }

    fun nextMonth() {
        if (_month.value == 12) {
            _month.value = 1
            _year.value += 1
        } else {
            _month.value += 1
        }
    }

    // --- Reset week offset when switching to week ---
    init {
        // Sync initial state with current date
        val cal = Calendar.getInstance()
        _year.value = cal.get(Calendar.YEAR)
        _month.value = cal.get(Calendar.MONTH) + 1
        _day.value = cal.get(Calendar.DAY_OF_MONTH)
    }

    private fun buildCategoryStats(
        summary: List<CategorySummary>,
        categories: List<Category>,
        totalAmount: Double
    ): List<CategoryStat> {
        if (totalAmount <= 0) return emptyList()
        return summary.mapNotNull { s ->
            val cat = categories.find { it.id == s.categoryId } ?: return@mapNotNull null
            CategoryStat(
                category = cat,
                total = s.total,
                percentage = (s.total / totalAmount).toFloat()
            )
        }.sortedByDescending { it.total }
    }
}
