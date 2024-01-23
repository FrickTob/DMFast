package com.example.dmfast.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.Navigation
import com.example.dmfast.models.BottomNavItem
import com.google.android.material.navigation.NavigationBarItemView
import com.google.gson.Gson
import java.io.File

@Composable
fun CampaignSplashScreen(selectedCmp : String, onNavigateToHome : () -> Unit) {
    val context = LocalContext.current
    val fileString = File(context.filesDir, "cmp$selectedCmp").readText()
    val gson = Gson()


    Column {
        TextButton(onClick = {
            Toast.makeText(context, "Current Cmp: $selectedCmp", Toast.LENGTH_SHORT).show()
            onNavigateToHome()
        }) {
            Text("back")
        }
        TextButton(onClick = {
            Toast.makeText(context, "File String $fileString", Toast.LENGTH_SHORT).show()
        }) {
            Text("Show String")
        }
        Text(selectedCmp)
        BottomNavigation()
    }
}

@Composable
fun BottomNavigation() {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Notes
    )

    NavigationBar {
        items.forEach {item ->
            NavItem(screen = item)
        }
    }
}

@Composable
fun RowScope.NavItem(screen: BottomNavItem) {
    NavigationBarItem(label = { Text(screen.title)},
                      selected = true,
                      onClick = { /*TODO*/ },
                      icon = { Icon(imageVector = screen.icon, contentDescription = screen.title) })
}