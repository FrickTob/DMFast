package com.example.dmfast

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.currentCompositionLocalContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
                    HomePage()
                }
            }
        }
    }
}


@Composable
fun HomePage() {
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
        val file = File(context.filesDir, "cmp$name").createNewFile()
        campaignNames = getCampaignNames()
    }

    var showDialog by remember { mutableStateOf(false) }



    if (showDialog) {
        CreateCmpDialog(onStateChange = {showDialog = it}, onCampaignNamed = {storeNewCampaign(it)})
    }
    Column(Modifier.fillMaxSize()) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Greeting("Android")
            Button(onClick = { showDialog = true }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Button")
            }
        }
        CampaignsList(campaigns = campaignNames, modifier = Modifier.weight(1F))
    }


}



@Composable
fun CreateCmpDialog(onStateChange : (Boolean) -> Unit, onCampaignNamed : (String) -> Unit) {
    val context = LocalContext.current
    var cmpTitleText by remember { mutableStateOf("") }
    Dialog(onDismissRequest = { onStateChange(false) }) {
        ElevatedCard {
            Column {
                Text("Name Your Campaign")
                TextField(value = cmpTitleText, onValueChange = { cmpTitleText = it })
                Row {
                    TextButton(onClick = { onStateChange(false) }) { Text("Cancel") }
                    TextButton(onClick = { onStateChange(false); onCampaignNamed(cmpTitleText) }) { Text("Confirm") }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}
@Composable
fun CampaignsList(campaigns : List<String>, modifier : Modifier) {
    if (campaigns.isNotEmpty()) {
        Column(modifier = modifier.verticalScroll(rememberScrollState())) {
            campaigns.forEach { campaign ->
                CampaignItem(name = campaign)
            }
        }
    }
    else {
        Text(text = "First Campaign? Press the + Button to create your first Campaign!")
    }
}

@Composable
fun CampaignItem(name: String) {
    val context = LocalContext.current
    Row(
        Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable {
                Toast
                    .makeText(context, "Row Clicked! $name", Toast.LENGTH_SHORT)
                    .show()
            }, horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(text = name, Modifier.padding(PaddingValues(start = 16.dp)), fontSize = 24.sp)
//        Button(onClick = { /*TODO*/ }, Modifier.padding(PaddingValues(end = 16.dp))) {
//            Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More Options")
//        }
        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More Options",
            Modifier
                .fillMaxHeight()
                .clickable { })
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DMFastTheme {
        Greeting("Android")
    }
}