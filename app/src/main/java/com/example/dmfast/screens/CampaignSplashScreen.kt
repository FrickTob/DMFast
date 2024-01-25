package com.example.dmfast.screens

import android.widget.EditText
import android.widget.Toast
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.Navigation
import com.example.dmfast.models.AppDatabase
import com.example.dmfast.models.BottomNavItem
import com.example.dmfast.models.Campaign
import com.example.dmfast.models.NavItems
import com.example.dmfast.models.Note
import com.google.android.material.navigation.NavigationBarItemView
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import kotlin.random.Random

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
        NavItems.Home -> Text("Campaign Home Screen")
        NavItems.Notes -> NotesScreen(campaign, db)
        NavItems.Characters -> Text("Campaign Characters Screen")
        NavItems.Encounters -> Text("Campaign Encounters Screen")
    }
}

@Composable
fun HomeScreen(campaign : Campaign) {

}

@Composable
fun MakeNoteDialog(campaign: Campaign,
                   db: AppDatabase,
                   onSubmitPress : (String, String) -> Unit,
                   onClosePress : () -> Unit) {
    val mainScope = CoroutineScope(Dispatchers.IO)
    var titleText by remember { mutableStateOf("") }
    var contentsText by remember { mutableStateOf("") }


// TODO: Add checks for proper note before submit
        Column {
            Text("Note Title")
            TextField(value = titleText, onValueChange = {titleText = it})
            Text("Description")
            TextField(value = contentsText, onValueChange = {contentsText = it} )
            Row {
                TextButton(onClick = { onClosePress() }) {
                    Text("Cancel")
                }
                TextButton(onClick = { onSubmitPress(titleText, contentsText) }) {
                    Text("Submit")
                }
            }
        }
}
@Composable
fun NotesScreen(campaign : Campaign, db : AppDatabase) {
    val mainScope = CoroutineScope(Dispatchers.IO)
    var showNoteEditScreen by remember { mutableStateOf(false) }
    var currNotes: List<Note> by remember { mutableStateOf(listOf()) }

    LaunchedEffect(Unit) {
        mainScope.launch {
            currNotes = db.noteDao().getALlForID(campaign.id)
        }
    }

    fun updateNotes() {
        mainScope.launch {
            currNotes = db.noteDao().getALlForID(campaign.id)
        }
    }

    fun submitNote(title : String, contents : String) {
        mainScope.launch {
            val newNote = Note(
                id = 0,
                cmpID = campaign.id,
                title = title,
                contents = contents
            )
            db.noteDao().insertAll(newNote)
            updateNotes()
        }
    }

    fun deleteNote(note : Note) {
        mainScope.launch {
            db.noteDao().delete(note)
            updateNotes()
        }
    }

    if (showNoteEditScreen) {
        MakeNoteDialog(campaign = campaign, db = db,
            onClosePress = {
                showNoteEditScreen = false},
            onSubmitPress = {title,contents ->
                submitNote(title, contents);
                showNoteEditScreen = false})
    }
    Column {
        Row(horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()) {
            Text("Notes")
            Button(onClick = { showNoteEditScreen = true }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Note button")
            }
        }
        Column {
            currNotes.forEach { note ->
                Row(horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier =  Modifier.fillMaxWidth()) {
                    Text(note.title)
                    TextButton(onClick = { deleteNote(note) }) {
                        Text("Delete")
                    }
                }
            }
        }
    }
}

@Composable
fun CharactersScreen(campaign : Campaign) {

}

@Composable
fun EncountersScreen(campaign : Campaign) {

}