package com.example.accountingapp.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.accountingapp.ui.components.TransactionItem
import com.example.accountingapp.ui.theme.BrownDark
import com.example.accountingapp.ui.theme.BrownLight
import com.example.accountingapp.ui.theme.BrownMedium
import com.example.accountingapp.ui.theme.CardBlue
import com.example.accountingapp.ui.theme.CardGreen
import com.example.accountingapp.ui.theme.CardPink
import com.example.accountingapp.ui.theme.Cream
import com.example.accountingapp.ui.theme.Mint
import com.example.accountingapp.ui.theme.MintDark
import com.example.accountingapp.ui.theme.Pink
import com.example.accountingapp.ui.theme.PinkDark
import com.example.accountingapp.ui.theme.Sunshine
import com.example.accountingapp.ui.theme.White
import java.util.Calendar

@Composable
fun HomeScreen(
    onAddClick: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Greeting header
            item {
                Spacer(modifier = Modifier.height(16.dp))
                GreetingHeader(state.currentMonth)
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Monthly summary cards
            item {
                MonthlySummaryCards(
                    income = state.monthIncome,
                    expense = state.monthExpense,
                    balance = state.monthBalance
                )
            }

            // Recent transactions header
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "最近账单",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "${state.currentYear}年${state.currentMonth}月",
                        style = MaterialTheme.typography.labelSmall,
                        color = BrownLight
                    )
                }
            }

            // Transaction list
            if (state.recentTransactions.isEmpty()) {
                item {
                    EmptyState()
                }
            } else {
                items(
                    items = state.recentTransactions,
                    key = { it.transaction.id }
                ) { tx ->
                    TransactionItem(
                        transaction = tx.transaction,
                        categoryEmoji = tx.categoryEmoji,
                        categoryName = tx.categoryName
                    )
                }
            }

            // Bottom spacing for FAB
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }

        // FAB
        FloatingActionButton(
            onClick = onAddClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .size(60.dp),
            shape = RoundedCornerShape(20.dp),
            containerColor = Pink,
            contentColor = White
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "记一笔",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun GreetingHeader(month: Int) {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when {
        hour < 6 -> "夜深了"
        hour < 9 -> "早上好"
        hour < 12 -> "上午好"
        hour < 14 -> "中午好"
        hour < 18 -> "下午好"
        else -> "晚上好"
    }

    Column {
        Text(
            text = "$greeting 👋",
            fontSize = 14.sp,
            color = BrownMedium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "小荷记账",
            style = MaterialTheme.typography.headlineLarge,
            color = BrownDark
        )
    }
}

@Composable
private fun MonthlySummaryCards(
    income: Double,
    expense: Double,
    balance: Double
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SummaryCard(
            modifier = Modifier.weight(1f),
            emoji = "💰",
            label = "收入",
            amount = income,
            amountColor = MintDark,
            bgColor = CardGreen
        )
        SummaryCard(
            modifier = Modifier.weight(1f),
            emoji = "💸",
            label = "支出",
            amount = expense,
            amountColor = Pink,
            bgColor = CardPink
        )
        SummaryCard(
            modifier = Modifier.weight(1f),
            emoji = "💎",
            label = "结余",
            amount = balance,
            amountColor = Sunshine,
            bgColor = CardBlue
        )
    }
}

@Composable
private fun SummaryCard(
    modifier: Modifier,
    emoji: String,
    label: String,
    amount: Double,
    amountColor: androidx.compose.ui.graphics.Color,
    bgColor: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = emoji, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                color = BrownMedium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "¥${String.format("%.0f", amount)}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = amountColor,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "📝", fontSize = 48.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "还没有账单记录",
            style = MaterialTheme.typography.bodyMedium,
            color = BrownLight
        )
        Text(
            text = "点击右下角 + 记一笔吧",
            style = MaterialTheme.typography.labelSmall,
            color = BrownLight
        )
    }
}
