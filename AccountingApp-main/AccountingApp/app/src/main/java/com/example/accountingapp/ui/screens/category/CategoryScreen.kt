package com.example.accountingapp.ui.screens.category

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.accountingapp.data.model.Category
import com.example.accountingapp.data.model.TransactionType
import com.example.accountingapp.ui.theme.BrownDark
import com.example.accountingapp.ui.theme.BrownLight
import com.example.accountingapp.ui.theme.BrownMedium
import com.example.accountingapp.ui.theme.CardGreen
import com.example.accountingapp.ui.theme.CardPink
import com.example.accountingapp.ui.theme.Cream
import com.example.accountingapp.ui.theme.Mint
import com.example.accountingapp.ui.theme.MintDark
import com.example.accountingapp.ui.theme.Pink
import com.example.accountingapp.ui.theme.PinkDark
import com.example.accountingapp.ui.theme.White

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CategoryScreen(
    viewModel: CategoryViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "分类管理",
                style = MaterialTheme.typography.headlineMedium,
                color = BrownDark
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "长按分类可删除，点击下方按钮新增分类",
                style = MaterialTheme.typography.labelSmall,
                color = BrownLight
            )
        }

        // Expense categories
        item {
            SectionHeader(
                emoji = "💸",
                title = "支出分类",
                count = state.expenseCategories.size,
                bgColor = CardPink,
                textColor = PinkDark
            )
        }

        items(state.expenseCategories, key = { it.id }) { category ->
            CategoryRow(
                category = category,
                onDelete = { viewModel.deleteCategory(category) }
            )
        }

        // Add expense button
        item {
            AddCategoryButton(
                label = "+ 添加支出分类",
                bgColor = Pink,
                onClick = { viewModel.showAddDialog(TransactionType.EXPENSE) }
            )
        }

        // Income categories
        item {
            Spacer(modifier = Modifier.height(6.dp))
            SectionHeader(
                emoji = "💰",
                title = "收入分类",
                count = state.incomeCategories.size,
                bgColor = CardGreen,
                textColor = MintDark
            )
        }

        items(state.incomeCategories, key = { it.id }) { category ->
            CategoryRow(
                category = category,
                onDelete = { viewModel.deleteCategory(category) }
            )
        }

        // Add income button
        item {
            AddCategoryButton(
                label = "+ 添加收入分类",
                bgColor = Mint,
                onClick = { viewModel.showAddDialog(TransactionType.INCOME) }
            )
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }

    // Add category dialog
    if (state.showAddDialog) {
        AddCategoryDialog(
            type = state.addDialogType,
            onConfirm = { name, emoji ->
                viewModel.addCategory(name, emoji, state.addDialogType)
            },
            onDismiss = { viewModel.dismissAddDialog() }
        )
    }
}

@Composable
private fun AddCategoryButton(
    label: String,
    bgColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = bgColor)
    ) {
        Text(label, color = White, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun AddCategoryDialog(
    type: TransactionType,
    onConfirm: (name: String, emoji: String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var emoji by remember { mutableStateOf(if (type == TransactionType.EXPENSE) "💸" else "💰") }

    val title = if (type == TransactionType.EXPENSE) "添加支出分类" else "添加收入分类"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("分类名称") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = emoji,
                    onValueChange = { emoji = it },
                    label = { Text("图标 Emoji") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name.ifBlank { "未命名" }, emoji.ifBlank { "📌" }) },
                enabled = name.isNotBlank()
            ) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@Composable
private fun SectionHeader(
    emoji: String,
    title: String,
    count: Int,
    bgColor: androidx.compose.ui.graphics.Color,
    textColor: androidx.compose.ui.graphics.Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = emoji, fontSize = 18.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$count 个",
            style = MaterialTheme.typography.labelSmall,
            color = BrownLight
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CategoryRow(category: Category, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {},
                onLongClick = onDelete
            ),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = category.emoji, fontSize = 26.sp)
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = category.name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
