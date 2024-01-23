package com.example.dmfast.screens

import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.dmfast.models.AppDatabase
import com.example.dmfast.ui.theme.DMFastTheme
import java.io.File

class MainActivity : ComponentActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DMFastTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(Dp(16F)),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    var selectedCmp by remember { mutableStateOf("") }
                    val db = Room.databaseBuilder(applicationContext,AppDatabase::class.java, "campaign-database").build()


                    NavHost(navController = navController, startDestination = "homePage") {
                        composable("homePage") {
                            HomePage(selectedCmp = selectedCmp,
                                     setSelectedCmp = {selectedCmp = it},
                                     db = db,
                                     onNavigateToSplash = {navController.navigate("cmpSplashScreen")})
                        }
                        composable("cmpSplashScreen") {
                            CampaignSplashScreen(selectedCmp = selectedCmp,
                                                 onNavigateToHome = {navController.navigate("homePage")})
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun HomePage(selectedCmp : String, setSelectedCmp : (String) -> Unit, db : AppDatabase, onNavigateToSplash : () -> Unit) {

    val context = LocalContext.current

    fun getCampaignNames(): List<String> {
        var files: List<String> = context.fileList().asList()
        files = files.filter { it.contains("cmp") }
        files = files.map { it.removePrefix("cmp") }
        files = files.map { it.replace('_', ' ') }
        return files
    }


    var campaignNames by remember { mutableStateOf(getCampaignNames()) }

    fun storeNewCampaign(name : String) {
        val fileCreated = File(context.filesDir, "cmp$name").createNewFile()
        if (!fileCreated) Toast.makeText(context, "Campaign Name Already Exists", Toast.LENGTH_SHORT).show()
        campaignNames = getCampaignNames()
    }

    fun deleteCampaign() {
        if (selectedCmp == "") return
        val fileDeleted = File(context.filesDir, "cmp$selectedCmp").delete()
        if (fileDeleted) Toast.makeText(context, "File Deleted Successfully", Toast.LENGTH_SHORT).show()
        else Toast.makeText(context, "Error Deleting File. Sorry, Please Try Again Another Time", Toast.LENGTH_SHORT).show()
        setSelectedCmp("")
        campaignNames = getCampaignNames()
    }

    fun renameCampaign(newName : String) {
        if (selectedCmp == "") return
        val newFileCreatedSuccessfully = File(context.filesDir, "cmp$newName").createNewFile()
        if (!newFileCreatedSuccessfully) {
            Toast.makeText(context, "Error Renaming. Sorry, Please Try Again Another Time", Toast.LENGTH_SHORT).show()
            setSelectedCmp("")
            return
        }
        val newFile = File(context.filesDir, "cmp$newName")
        val fileRenamed = File(context.filesDir, "cmp$selectedCmp").renameTo(newFile)
        if (!fileRenamed) {
            Toast.makeText(context, "Error Renaming. Sorry, Please Try Again Another Time", Toast.LENGTH_SHORT).show()
            setSelectedCmp("")
            return
        }
        Toast.makeText(context, "File Rename Success!", Toast.LENGTH_SHORT).show()
        campaignNames = getCampaignNames()
    }

    var showCreateCmpDialog by remember { mutableStateOf(false) }
    var showRenameCmpDialog by remember { mutableStateOf(false)}
    var showDeleteCmpDialog by remember { mutableStateOf(false)}



    if (showCreateCmpDialog) {
        CreateCmpDialog(closeDialog = {showCreateCmpDialog = false}, onCampaignNamed = {storeNewCampaign(it)})
    }
    if (showRenameCmpDialog) {
        RenameCmpDialog(prevName = selectedCmp, closeDialog = { showRenameCmpDialog = false }, onCampaignRenamed = {renameCampaign(it)})
    }
    if (showDeleteCmpDialog) {
        DeleteCmpConfirmationDialog(closeDialog = {showDeleteCmpDialog = false}, onCmpDeleted = {deleteCampaign()})
    }
    Column(Modifier.fillMaxSize()) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text("Fast DM")
            Button(onClick = { showCreateCmpDialog = true }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Button")
            }
        }
        CampaignsList(campaigns = campaignNames,
                      updateSelectedCmp = {setSelectedCmp(it)},
                      onShowDeleteDialog = {showDeleteCmpDialog = it},
                      onShowRenameDialog = {showRenameCmpDialog = it},
                      onNavigateToSplash = onNavigateToSplash,
                      modifier = Modifier.weight(1F))
    }


}



@Composable
fun CreateCmpDialog(closeDialog : () -> Unit, onCampaignNamed : (String) -> Unit) {
    val context = LocalContext.current
    var cmpTitleText by remember { mutableStateOf("") }
    Dialog(onDismissRequest = { closeDialog() }) {
        ElevatedCard {
            Column {
                Text("Name Your Campaign")
                TextField(value = cmpTitleText,
                          onValueChange = { cmpTitleText = it },
                          singleLine = true,
                          modifier = Modifier.onKeyEvent { if (it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                              closeDialog()
                              onCampaignNamed(cmpTitleText)
                              true
                          }
                              false
                          })
                Row {
                    TextButton(onClick = { closeDialog() }) { Text("Cancel") }
                    TextButton(onClick = { closeDialog(); onCampaignNamed(cmpTitleText) }) { Text("Confirm") }
                }
            }
        }
    }
}

@Composable
fun RenameCmpDialog(prevName: String , closeDialog: () -> Unit, onCampaignRenamed : (String) -> Unit) {
    val context = LocalContext.current
    var cmpTitleText by remember { mutableStateOf(prevName) }
    Dialog(onDismissRequest = { closeDialog() }) {
        ElevatedCard {
            Column {
                Text("Rename Campaign")
                TextField(value = cmpTitleText,
                          onValueChange = { cmpTitleText = it },
                          singleLine = true,
                          modifier = Modifier.onKeyEvent { if (it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                              closeDialog()
                              onCampaignRenamed(cmpTitleText)
                              true
                          }
                              false
                          })
                Row {
                    TextButton(onClick = { closeDialog() }) { Text("Cancel") }
                    TextButton(onClick = { closeDialog(); onCampaignRenamed(cmpTitleText) }) { Text("Confirm") }
                }
            }
        }
    }
}

@Composable
fun DeleteCmpConfirmationDialog(closeDialog: () -> Unit, onCmpDeleted : () -> Unit) {
    AlertDialog(onDismissRequest = { closeDialog() },
                title = { Text(text = "Are you sure?")},
                dismissButton = {
                    TextButton(onClick = { closeDialog() }) {
                        Text("Cancel")
                    }
                },
                confirmButton = {
                    TextButton(onClick = { closeDialog(); onCmpDeleted()}) {
                        Text("Delete Campaign")
                    }
                })
}

@Composable
fun CampaignsList(campaigns : List<String>,
                  updateSelectedCmp: (String) -> Unit,
                  onShowDeleteDialog: (Boolean) -> Unit,
                  onShowRenameDialog: (Boolean) -> Unit,
                  onNavigateToSplash: () -> Unit,
                  modifier : Modifier) {
    if (campaigns.isNotEmpty()) {
        Column(modifier = modifier.verticalScroll(rememberScrollState())) {
            campaigns.forEach { campaign ->
                CampaignItem(name = campaign, updateSelectedCmp = updateSelectedCmp, onShowDeleteDialog = onShowDeleteDialog, onShowRenameDialog = onShowRenameDialog, onNavigateToSplash = onNavigateToSplash)
            }
        }
    }
    else {
        Text(text = "First Campaign? Press the + Button to create your first Campaign!")
    }
}

@Composable
fun CampaignItem(name: String,
                 updateSelectedCmp : (String) -> Unit,
                 onShowDeleteDialog : (Boolean) -> Unit,
                 onShowRenameDialog : (Boolean) -> Unit,
                 onNavigateToSplash: () -> Unit) {
    var showDropdown by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Row(
        Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable {
                updateSelectedCmp(name)
                onNavigateToSplash()
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
            Text(text = name, Modifier.padding(PaddingValues(start = 16.dp)), fontSize = 24.sp)
        Box {
            Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More Options",
                Modifier
                    .fillMaxHeight()
                    .clickable { showDropdown = true; updateSelectedCmp(name) })
            DropdownMenu(expanded = showDropdown, onDismissRequest = { showDropdown = false}) {
                TextButton(onClick = { showDropdown = false; onShowRenameDialog(true) }, Modifier.fillMaxWidth()) {
                    Text("Rename", Modifier.fillMaxWidth())
                }
                TextButton(onClick = { showDropdown = false; onShowDeleteDialog(true) }, Modifier.fillMaxWidth()) {
                    Text("Delete Campaign", Modifier.fillMaxWidth())
                }
            }
        }
        }
}