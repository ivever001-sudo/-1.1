package com.example.accountingapp.ui.screens.add

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.accountingapp.AccountingApp
import com.example.accountingapp.data.model.Category
import com.example.accountingapp.data.model.Transaction
import com.example.accountingapp.data.model.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddTransactionUiState(
    val type: TransactionType = TransactionType.EXPENSE,
    val amount: String = "",
    val selectedCategoryId: Long? = null,
    val categories: List<Category> = emptyList(),
    val note: String = "",
    val date: Long = System.currentTimeMillis(),
    val saved: Boolean = false
)

class AddTransactionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as AccountingApp).repository

    private val _state = MutableStateFlow(AddTransactionUiState())
    val uiState: StateFlow<AddTransactionUiState> = _state.asStateFlow()

    val allCategories: StateFlow<List<Category>> = repository.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setType(type: TransactionType) {
        _state.update { it.copy(type = type, selectedCategoryId = null) }
    }

    fun setAmount(value: String) {
        if (value.isEmpty() || value.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
            _state.update { it.copy(amount = value) }
        }
    }

    fun selectCategory(categoryId: Long) {
        _state.update { it.copy(selectedCategoryId = categoryId) }
    }

    fun setNote(value: String) {
        _state.update { it.copy(note = value) }
    }

    fun setDate(timestamp: Long) {
        _state.update { it.copy(date = timestamp) }
    }

    fun save() {
        val current = _state.value
        val amount = current.amount.toDoubleOrNull() ?: return
        val categoryId = current.selectedCategoryId ?: return
        if (amount <= 0) return

        viewModelScope.launch {
            repository.insertTransaction(
                Transaction(
                    amount = amount,
                    type = current.type,
                    categoryId = categoryId,
                    note = current.note,
                    date = current.date
                )
            )
            _state.update { it.copy(saved = true) }
        }
    }
}
