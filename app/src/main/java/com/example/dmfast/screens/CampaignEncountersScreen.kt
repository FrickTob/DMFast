package com.example.dmfast.screens

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.dmfast.models.AppDatabase
import com.example.dmfast.models.Campaign
import com.example.dmfast.models.Enemy
import com.example.dmfast.models.PlayableCharacter

@Composable
fun EncountersScreen(campaign : Campaign, db : AppDatabase) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    var monstersList : List<Enemy> by remember { mutableStateOf(listOf()) }
    var filteredMonstersList : List<Enemy> by remember { mutableStateOf(listOf()) }
    var selectedCR : Double by remember { mutableDoubleStateOf(-1.0) }
    var dropdownExpanded : Boolean by remember { mutableStateOf(false) }
    var currentEnemies : List<Enemy> by remember { mutableStateOf(listOf()) }
    var players : List<PlayableCharacter> by remember { mutableStateOf(listOf()) }
    var currContext = LocalContext.current

    LaunchedEffect(Unit) {
        monstersList = db.enemyDao().getAll()
        players = listOf(PlayableCharacter(id = 0, cmpID = 0, name = "Toby", charClass = "Druid"))
        filteredMonstersList = monstersList
    }
    LaunchedEffect(selectedCR) {
        filteredMonstersList = monstersList.filter { enemy ->
            enemy.cr == selectedCR
        }
    }
    Column(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight()) {
        Text("Challenge Rating", Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        CRSelectRow(onCRSelected = {selectedCR = it}, selectedCR = selectedCR, screenWidth = screenWidth)
        Text("Monsters")
        AvailableEnemiesBox(filteredMonstersList, screenHeight, onAddMonster = {currentEnemies += it})
        Text("Current Encounter")
        CurrentEncounterBox(currentEnemies, screenHeight, onRemoveMonster = {/*TODO*/})
        Text("Difficulty")

        // Save encounter / load encounter button

        // Bottom menu to see current party and level which can be adjusted
    }


}

@Composable
fun CRSelectRow(onCRSelected : (Double) -> Unit, selectedCR : Double, screenWidth : Dp) {
    val squareSize = screenWidth / 8
    val crsList = doubleArrayOf(0.0, .125, .25, .5, 1.0, 2.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0,
                                13.0, 14.0, 15.0, 16.0, 17.0, 19.0, 20.0, 21.0, 22.0, 23.0, 24.0, 30.0)
    Row(modifier = Modifier
        .horizontalScroll(rememberScrollState())
        .fillMaxWidth(),
        horizontalArrangement =  Arrangement.SpaceEvenly ,
        verticalAlignment = Alignment.CenterVertically) {
        Spacer(Modifier.width(squareSize / 3))
        crsList.forEach {
            Box(Modifier
                .height(squareSize)
                .width(squareSize)
                .clip(RoundedCornerShape(squareSize / 4))
                .clickable { onCRSelected(it) }
                .background(if (it == selectedCR) Color.DarkGray else Color.Gray),
                contentAlignment = Alignment.Center) {
                Text(it.toString(), color = Color.White)
            }
            Spacer(Modifier.width(squareSize / 3))
        }
    }
}

@Composable
fun AvailableEnemiesBox(monstersList : List<Enemy>, screenHeight : Dp, onAddMonster : (Enemy) -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .height(screenHeight / 5)
            .verticalScroll(rememberScrollState())) {
        monstersList.forEach {enemy ->
            Box(Modifier.fillMaxWidth()) {
                Text(enemy.name)
            }
            Spacer(Modifier.height(3.dp))
        }
    }
}

@Composable
fun CurrentEncounterBox(currentEnemies : List<Enemy>, screenHeight : Dp, onRemoveMonster : (Enemy) -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .height(screenHeight / 5)
            .verticalScroll(rememberScrollState())) {
        currentEnemies.forEach {enemy ->
            Box(Modifier.fillMaxWidth()) {
                Text(enemy.name)
            }
            Spacer(Modifier.height(3.dp))
        }
    }
}