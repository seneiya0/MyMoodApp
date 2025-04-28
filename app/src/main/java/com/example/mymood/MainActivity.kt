package com.example.mymood

import android.os.Bundle
import android.text.Layout
import androidx.compose.ui.Alignment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mymood.data.MoodDao
import com.example.mymood.data.MoodRepository
import com.example.mymood.model.MoodEntry
import com.example.mymood.ui.theme.MyMoodTheme
import com.example.mymood.viewmodel.MoodViewModel
import com.example.mymood.viewmodel.MoodViewModelFactory
import kotlinx.coroutines.flow.collect
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.mymood.data.MoodDatabase
import com.example.mymood.ui.HelpScreen
import com.example.mymood.ui.PreferencesScreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.mymood.ui.navigation.BottomNavigationBar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyMoodTheme {
                val navController = rememberNavController()
                val database = remember {
                    Room.databaseBuilder(
                        applicationContext,
                        MoodDatabase::class.java,
                        "mood_database"
                    )
                        .fallbackToDestructiveMigration(false)
                        .build()
                }

                val repository = remember { MoodRepository(database.moodDao()) }

                val moodViewModel: MoodViewModel = viewModel(
                    factory = MoodViewModelFactory(repository)
                )

                Scaffold(
                    bottomBar = { BottomNavigationBar(navController = navController) }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "moodTracker",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(route = "moodTracker") {
                            MoodTrackerScreen(navController = navController, viewModel = moodViewModel)
                        }
                        composable(route = "preferences") {
                            PreferencesScreen(navController = navController)
                        }
                        composable(route = "help") {
                            HelpScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodTrackerScreen(viewModel: MoodViewModel, navController: NavController) {
    var selectedMood by remember { mutableStateOf("😊") }
    var notes by remember { mutableStateOf(TextFieldValue("")) }
    var showHistory by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    val sleepOptions = listOf("1 hour","2 hours","3 hours","4 hours", "5 hours", "6 hours", "7 hours", "8 hours", "9 hours", "10 hours", "11 hours", "12 hours", "Over 12 Hours")
    var selectedSleep by remember { mutableStateOf(sleepOptions[2]) }
    var stressLevel by remember { mutableStateOf(5f) }

    val moodList by viewModel.moodEntries.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .padding(WindowInsets.systemBars.asPaddingValues())
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "How are you feeling today?",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            listOf("😊", "😐", "😢").forEach { mood ->
                Button(
                    onClick = { selectedMood = mood },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedMood == mood)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.secondary
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                ) {
                    Text(text = mood, style = MaterialTheme.typography.headlineMedium)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = selectedSleep,
                onValueChange = {},
                readOnly = true,
                label = { Text("Sleep Hours") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                sleepOptions.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption) },
                        onClick = {
                            selectedSleep = selectionOption
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column {
            Text(
                text = "Stress Level: ${stressLevel.toInt()}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Slider(
                value = stressLevel,
                onValueChange = { stressLevel = it },
                valueRange = 0f..10f,
                steps = 9,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Notes Box (already had)
        BasicTextField(
            value = notes,
            onValueChange = { notes = it },
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(8.dp),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(color = MaterialTheme.colorScheme.tertiary)
                        .padding(8.dp)
                ) {
                    if (notes.text.isEmpty()) {
                        Text(
                            text = "Add an optional note...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    innerTextField()
                }
            }
        )

        Spacer(modifier = Modifier.padding(8.dp))

        Button(
            onClick = {
                viewModel.addMood(selectedMood, notes.text, selectedSleep, stressLevel.toInt())
                notes = TextFieldValue("")
            },
            modifier = Modifier.fillMaxWidth().height(72.dp).padding(start = 24.dp, end = 24.dp),

        ) {
            Text("Save Mood")
        }

        Button(
            onClick = { showHistory = true },
            modifier = Modifier.fillMaxWidth().padding(20.dp)
        ) {
            Text("View Mood History")
        }

        if (showHistory) {
            AlertDialog(
                onDismissRequest = { showHistory = false },
                confirmButton = {
                    TextButton(onClick = { showHistory = false }) {
                        Text("Close")
                    }
                },
                title = {
                    Text("Mood History")
                },
                text = {
                    if (moodList.isEmpty()) {
                        Text("No moods recorded yet.")
                    } else {
                        LazyColumn {
                            items(moodList) { mood ->
                                Divider(modifier = Modifier.padding(vertical = 4.dp))
                                Text("${mood.mood} - ${mood.notes.orEmpty()} | ${formatTimestamp(mood.timestamp)}")
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }
                    }
                }
            )
        }
    }
}

fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

@Preview(showBackground = true)
@Composable
fun MoodTrackerScreenPreview() {
    val navController = rememberNavController()

    val fakeViewModel = object : MoodViewModel(
        MoodRepository(
            object : MoodDao {
                override suspend fun insertMood(moodEntry: MoodEntry) {}
                override fun getAllMoods(): kotlinx.coroutines.flow.Flow<List<MoodEntry>> =
                    kotlinx.coroutines.flow.flowOf(
                        listOf(
                            MoodEntry(
                                mood = "😊",
                                notes = "Feeling good!",
                                sleepHours = "8",
                                stressLevel = 2,
                                timestamp = System.currentTimeMillis()
                            ),
                            MoodEntry(
                                mood = "😐",
                                notes = "Okay day.",
                                sleepHours = "6",
                                stressLevel = 5,
                                timestamp = System.currentTimeMillis()
                            )
                        )
                    )
            }
        )
    ) {}

    MyMoodTheme {
        MoodTrackerScreen(
            viewModel = fakeViewModel,
            navController = navController
        )
    }
}