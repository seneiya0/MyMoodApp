package com.example.mymood

import android.os.Bundle
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    private val database by lazy {
        androidx.room.Room.databaseBuilder(
            applicationContext,
            com.example.mymood.data.MoodDatabase::class.java,
            "mood_database"
        ).build()
    }

    private val moodViewModel: MoodViewModel by viewModels {
        MoodViewModelFactory(
            MoodRepository(database.moodDao())
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyMoodTheme {
                MoodTrackerScreen(moodViewModel)
            }
        }
    }
}

@Composable
fun MoodTrackerScreen(viewModel: MoodViewModel) {
    var selectedMood by remember { mutableStateOf("😊") }
    var notes by remember { mutableStateOf(TextFieldValue("")) }
    var showHistory by remember { mutableStateOf(false) }
    val moodList by viewModel.moodEntries.collectAsState()

    Column(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .padding(WindowInsets.systemBars.asPaddingValues())
            .padding(16.dp)
    ) {
        Text(
            text = "How are you feeling today?",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
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

        BasicTextField(
            value = notes,
            onValueChange = { notes = it },
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(8.dp)
            ,
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

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.addMood(selectedMood, notes.text)
                notes = TextFieldValue("")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Mood")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { showHistory = true },
            modifier = Modifier.fillMaxWidth()
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
    val fakeViewModel = object : MoodViewModel(
        MoodRepository(
            object : MoodDao {
                override suspend fun insertMood(moodEntry: MoodEntry) {}
                override fun getAllMoods() = kotlinx.coroutines.flow.flowOf(
                    listOf(
                        MoodEntry(mood = "😊", notes = "Feeling good!", timestamp = System.currentTimeMillis()),
                        MoodEntry(mood = "😐", notes = "Okay day.", timestamp = System.currentTimeMillis())
                    )
                )
            }
        )
    ) {}
    MyMoodTheme {
        MoodTrackerScreen(viewModel = fakeViewModel)
    }
}