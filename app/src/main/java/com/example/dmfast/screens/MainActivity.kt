package com.example.dmfast.screens

import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.updateTransition
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.dmfast.models.AppDatabase
import com.example.dmfast.models.Campaign
import com.example.dmfast.ui.theme.DMFastTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID
import kotlin.random.Random

class MainActivity : ComponentActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DMFastTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    var selectedCmp: Campaign? by remember { mutableStateOf(null ) }
                    val db = Room.databaseBuilder(applicationContext,AppDatabase::class.java, "campaign-database").fallbackToDestructiveMigration().build()



                    NavHost(navController = navController, startDestination = "homePage") {
                        composable("homePage") {
                            HomePage(selectedCmp = selectedCmp,
                                     setSelectedCmp = {selectedCmp = it},
                                     db = db,
                                     onNavigateToSplash = {navController.navigate("cmpSplashScreen")})
                        }
                        composable("cmpSplashScreen") {
                            CampaignSplashScreen(selectedCmp = selectedCmp!!,
                                                 onNavigateToHome = {navController.navigate("homePage")},
                                                 db = db)
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun HomePage(selectedCmp : Campaign?, setSelectedCmp : (Campaign?) -> Unit, db : AppDatabase, onNavigateToSplash : () -> Unit) {
    val mainScope = CoroutineScope(Dispatchers.IO)
    val context = LocalContext.current


    var campaignNames : List<Campaign> by remember { mutableStateOf(listOf()) }

    fun updateCampaigns() {
        mainScope.launch {
            campaignNames = db.campaignDao().getAll()
        }
    }

    fun storeNewCampaign(name : String) {
        // make campaign object and add it
        val newCmp = Campaign(id = 0, cmpName = name)
        mainScope.launch {
            db.campaignDao().insertAll(newCmp)
            updateCampaigns()
        }
    }

    fun deleteCampaign() {
        if (selectedCmp == null) return
        mainScope.launch {
            db.campaignDao().delete(selectedCmp)
            updateCampaigns()
        }
    }

    fun renameCampaign(newName : String) {
        if (selectedCmp == null) return
        mainScope.launch {
            val newCmp = Campaign(selectedCmp.id, newName)
            db.campaignDao().delete(selectedCmp)
            db.campaignDao().insertAll(newCmp)
            updateCampaigns()
        }
    }

    var showCreateCmpDialog by remember { mutableStateOf(false) }
    var showRenameCmpDialog by remember { mutableStateOf(false)}
    var showDeleteCmpDialog by remember { mutableStateOf(false)}


    LaunchedEffect(Unit) {
        updateCampaigns()
    }

    if (showCreateCmpDialog) {
        CreateCmpDialog(closeDialog = {showCreateCmpDialog = false}, onCampaignNamed = {storeNewCampaign(it)})
    }
    if (showRenameCmpDialog) {
        RenameCmpDialog(prevCampaign = selectedCmp!!, closeDialog = { showRenameCmpDialog = false }, onCampaignRenamed = {renameCampaign(it)})
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
        Text("Your Campaigns")
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
fun RenameCmpDialog(prevCampaign: Campaign , closeDialog: () -> Unit, onCampaignRenamed : (String) -> Unit) {
    val context = LocalContext.current
    var cmpTitleText by remember { mutableStateOf(prevCampaign.cmpName) }
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
fun CampaignsList(campaigns : List<Campaign>,
                  updateSelectedCmp: (Campaign?) -> Unit,
                  onShowDeleteDialog: (Boolean) -> Unit,
                  onShowRenameDialog: (Boolean) -> Unit,
                  onNavigateToSplash: () -> Unit,
                  modifier : Modifier) {
    if (campaigns.isNotEmpty()) {
        Column(modifier = modifier.verticalScroll(rememberScrollState())) {
            campaigns.forEach { campaign ->
                CampaignItem(campaign = campaign, updateSelectedCmp = updateSelectedCmp, onShowDeleteDialog = onShowDeleteDialog, onShowRenameDialog = onShowRenameDialog, onNavigateToSplash = onNavigateToSplash)
            }
        }
    }
    else {
        Text(text = "First Campaign? Press the + Button to create your first Campaign!")
    }
}

@Composable
fun CampaignItem(campaign: Campaign?,
                 updateSelectedCmp : (Campaign?) -> Unit,
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
                updateSelectedCmp(campaign)
                onNavigateToSplash()
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
            if (campaign != null) {
                Text(text = campaign.cmpName, Modifier.padding(PaddingValues(start = 16.dp)), fontSize = 24.sp)
            }
        Box {
            Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More Options",
                Modifier
                    .fillMaxHeight()
                    .clickable { showDropdown = true; updateSelectedCmp(campaign) })
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