package com.example.dmfast.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.Navigation
import com.example.dmfast.models.BottomNavItem
import com.example.dmfast.models.Campaign
import com.example.dmfast.models.NavItems
import com.google.android.material.navigation.NavigationBarItemView
import com.google.gson.Gson
import java.io.File

@Composable
fun CampaignSplashScreen(selectedCmp : Campaign, onNavigateToHome : () -> Unit) {
    val context = LocalContext.current

    var selectedNavItem by remember { mutableStateOf(NavItems.Home) }


    Column(Modifier) {
        TextButton(onClick = {
            Toast.makeText(context, "Current Cmp: $selectedCmp", Toast.LENGTH_SHORT).show()
            onNavigateToHome()
        }) {
            Text("back")
        }
        Column(Modifier.weight(1f)) {
            SelectedScreen(selectedNavItem)
        }
        BottomNavigation(selectedNavItem, onSelectNavItem = {selectedNavItem = it})
    }
}
@Composable
fun SelectedScreen(selectedNavItem : NavItems) {
    when (selectedNavItem) {
        NavItems.Home -> Text("Campaign Home Screen")
        NavItems.Notes -> Text("Campaign Notes Screen")
        NavItems.Characters -> Text("Campaign Characters Screen")
        NavItems.Encounters -> Text("Campaign Encounters Screen")
    }
}


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
    NavigationBarItem(label = { Text(screen.title)},
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