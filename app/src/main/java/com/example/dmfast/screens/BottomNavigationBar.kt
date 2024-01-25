package com.example.dmfast.screens

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.dmfast.models.BottomNavItem
import com.example.dmfast.models.NavItems

@Composable
fun BottomNavigation(selectedNavItem : NavItems, onSelectNavItem : (NavItems) -> Unit) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Notes,
        BottomNavItem.Characters,
        BottomNavItem.Encounters
    )

    NavigationBar {
        items.forEach {item ->
            NavItem(screen = item, selectedNavItem, onSelectNavItem)
        }
    }
}

@Composable
fun RowScope.NavItem(screen: BottomNavItem, selectedNavItem : NavItems, onSelectNavItem: (NavItems) -> Unit) {
    NavigationBarItem(label = { Text(screen.title) },
        selected = when (selectedNavItem) {
            NavItems.Home -> screen == BottomNavItem.Home
            NavItems.Notes -> screen == BottomNavItem.Notes
            NavItems.Characters -> screen == BottomNavItem.Characters
            NavItems.Encounters -> screen == BottomNavItem.Encounters
        },
        onClick = {
            when (screen) {
                BottomNavItem.Home -> onSelectNavItem(NavItems.Home)
                BottomNavItem.Notes -> onSelectNavItem(NavItems.Notes)
                BottomNavItem.Characters -> onSelectNavItem(NavItems.Characters)
                BottomNavItem.Encounters -> onSelectNavItem(NavItems.Encounters)
            }
        },
        icon = { Icon(imageVector = screen.icon, contentDescription = screen.title) })
}