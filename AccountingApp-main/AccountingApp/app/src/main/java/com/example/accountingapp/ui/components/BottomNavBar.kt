package com.example.accountingapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.accountingapp.ui.navigation.Screen
import com.example.accountingapp.ui.theme.Cream
import com.example.accountingapp.ui.theme.Pink
import com.example.accountingapp.ui.theme.White

data class BottomNavItem(
    val label: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem("首页", Screen.Home.route, Icons.Filled.Home, Icons.Outlined.Home),
    BottomNavItem("统计", Screen.Stats.route, Icons.Filled.DateRange, Icons.Outlined.DateRange),
    BottomNavItem("分类", Screen.Category.route, Icons.Filled.List, Icons.Outlined.List)
)

@Composable
fun BottomNavBar(
    currentRoute: String?,
    onNavigate: (Screen) -> Unit
) {
    NavigationBar(
        containerColor = White
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    when (item.route) {
                        Screen.Home.route -> onNavigate(Screen.Home)
                        Screen.Stats.route -> onNavigate(Screen.Stats)
                        Screen.Category.route -> onNavigate(Screen.Category)
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Pink,
                    selectedTextColor = Pink,
                    indicatorColor = Cream
                )
            )
        }
    }
}
