package com.example.dmfast.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.dmfast.R

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

}