package com.example.dmfast.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import com.example.dmfast.models.AppDatabase
import com.example.dmfast.models.Campaign
import com.example.dmfast.models.Note
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun EditNoteView(campaign: Campaign,
             selectedNote : Note?,
             db: AppDatabase,
             onSubmitPress : (String) -> Unit,
             onClosePress : () -> Unit) {
    val mainScope = CoroutineScope(Dispatchers.IO)
    var contentsText by remember { mutableStateOf(selectedNote?.contents ?: "") }


// TODO: Add checks for proper note before submit
    Column(Modifier.fillMaxWidth()) {
        Text("Description")
        TextField(value = contentsText,
            onValueChange = {contentsText = it},
            maxLines = 1000,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth())
        Row {
            TextButton(onClick = { onClosePress() }) {
                Text("Cancel")
            }
            TextButton(onClick = { onSubmitPress(contentsText) }) {
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
    var selectedNote: Note? by remember { mutableStateOf(null) }

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

    fun submitNote(contents : String) {
        val lines = contents.split("\n")
        val title = lines[0] + "\n"
        lines.drop(0)
        val description = lines.reduce {
                acc, s ->  "$acc\n$s"
        }
        if (selectedNote == null) { // Creating New Note
            mainScope.launch {
                val newNote = Note(
                    id = 0,
                    cmpID = campaign.id,
                    title = title,
                    contents = description
                )
                db.noteDao().insertAll(newNote)
                updateNotes()
            }
        }
        else { // Update Existing Note
            mainScope.launch {
                selectedNote!!.title = title
                selectedNote!!.contents = description
                db.noteDao().updateNote(selectedNote!!)}
        }
    }

    fun deleteNote(note : Note) {
        mainScope.launch {
            db.noteDao().delete(note)
            updateNotes()
        }
        selectedNote = null
    }

    if (showNoteEditScreen) {
        EditNoteView(campaign = campaign, db = db, selectedNote = selectedNote,
            onClosePress = {
                showNoteEditScreen = false},
            onSubmitPress = {contents ->
                submitNote(contents);
                showNoteEditScreen = false})
    }
    else {
        Column {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Notes")
                Button(onClick = { showNoteEditScreen = true }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Note button")
                }
            }
            Column {
                currNotes.forEach { note ->
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedNote = note
                                showNoteEditScreen = true
                            }
                    ) {
                        Text(note.title)
                        TextButton(onClick = { deleteNote(note) }) {
                            Text("Delete")
                        }
                    }
                }
            }
        }
    }
}