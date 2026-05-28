package com.example.accountingapp.ui.screens.category

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.accountingapp.AccountingApp
import com.example.accountingapp.data.model.Category
import com.example.accountingapp.data.model.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CategoryUiState(
    val incomeCategories: List<Category> = emptyList(),
    val expenseCategories: List<Category> = emptyList(),
    val showAddDialog: Boolean = false,
    val addDialogType: TransactionType = TransactionType.EXPENSE
)

class CategoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as AccountingApp).repository

    private val _categories = combine(
        repository.getCategoriesByType(TransactionType.INCOME),
        repository.getCategoriesByType(TransactionType.EXPENSE)
    ) { income, expense -> income to expense }

    private val _showAddDialog = MutableStateFlow(false)
    private val _addDialogType = MutableStateFlow(TransactionType.EXPENSE)

    val uiState: StateFlow<CategoryUiState> = combine(
        _categories, _showAddDialog, _addDialogType
    ) { (income, expense), showDialog, dialogType ->
        CategoryUiState(
            incomeCategories = income,
            expenseCategories = expense,
            showAddDialog = showDialog,
            addDialogType = dialogType
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CategoryUiState())

    fun showAddDialog(type: TransactionType) {
        _addDialogType.value = type
        _showAddDialog.value = true
    }

    fun dismissAddDialog() {
        _showAddDialog.value = false
    }

    fun addCategory(name: String, emoji: String, type: TransactionType) {
        viewModelScope.launch {
            repository.insertCategory(
                Category(name = name, emoji = emoji, type = type)
            )
            _showAddDialog.value = false
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            repository.deleteCategory(category)
        }
    }
}
