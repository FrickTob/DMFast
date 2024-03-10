package com.example.dmfast.screens

import android.media.MediaParser
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.dmfast.models.AppDatabase
import com.example.dmfast.models.Campaign
import com.example.dmfast.models.NavItems
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

@Composable
fun CampaignSplashScreen(selectedCmp : Campaign, onNavigateToHome : () -> Unit, db : AppDatabase) {
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
            SelectedScreen(selectedNavItem, selectedCmp, db)
        }
        BottomNavigation(selectedNavItem, onSelectNavItem = {selectedNavItem = it})
    }
}
@Composable
fun SelectedScreen(selectedNavItem : NavItems, campaign: Campaign, db: AppDatabase) {
    when (selectedNavItem) {
        NavItems.Home -> HomeScreen(campaign = campaign)
        NavItems.Notes -> NotesScreen(campaign, db)
        NavItems.Characters -> CharactersScreen(campaign = campaign)
        NavItems.Encounters -> EncountersScreen(campaign = campaign, db)
    }
}