package com.example.dmfast.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.dmfast.R

enum class NavItems {
    Home, Notes, Characters, Encounters
}

sealed class BottomNavItem(
    val title : String,
    val icon : ImageVector
)
{
    data object Home :
            BottomNavItem(
                "Home",
                Icons.Default.Home
            )

    data object Notes :
        BottomNavItem(
            "Notes",
            Icons.Default.Create
        )

    data object Characters :
            BottomNavItem(
                "Characters",
                Icons.Default.AccountBox
            )

    data object Encounters :
            BottomNavItem(
                "Encounters",
                Icons.Default.Close
            )

}