package com.example.mymood.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mymood.formatTimestamp
import com.example.mymood.viewmodel.MoodViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DataDisplayScreen(
    navController: NavController,
    viewModel: MoodViewModel
) {
    val moodListState = viewModel.moodEntries.collectAsState(initial = emptyList())
    val moodList = moodListState.value
    var showHistory by remember { mutableStateOf(false) }

    val averageSleep = moodList.mapNotNull{ entry ->
        entry.sleepHours.filter {it.isDigit() }.toIntOrNull()}
        .average().takeIf {!it.isNaN() }?.let {String.format("%.1f", it)}?: "N/A"

    val averageStress = moodList.map{it.stressLevel}.average()
        .takeIf { !it.isNaN() }?.let{String.format("%.1f", it)}?: "N/A"

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Mood Summary",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
            )
        Spacer(modifier = Modifier.height(8.dp))
        Divider()
        Spacer(modifier = Modifier.height(12.dp))
        Row(){
            Text("Average Sleep Hours: ",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(averageSleep,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.secondary
            )
            val avgSleepNum = averageSleep.toFloatOrNull()
            if (avgSleepNum != null && avgSleepNum < 7) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Low Sleep Warning",
                    tint = MaterialTheme.colorScheme.error
                )
                Text(
                    "Try to get more sleep!",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }
        }

        Row(){
            Text("Average Stress Level: ",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(averageStress,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.secondary
            )
            val avgStressNum = averageStress.toFloatOrNull()
            if (avgStressNum != null && avgStressNum > 6) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "High Stress Warning",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }


        Spacer(modifier = Modifier.height(16.dp))
        Divider()
        Spacer(modifier = Modifier.height(16.dp))

        Text("Recent Mood Entries",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
            )
        Spacer(modifier = Modifier.height(8.dp))

        if (moodList.isEmpty()) {
            Text("No mood entries yet.",
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            LazyColumn(modifier = Modifier
                .fillMaxSize()
                .weight(1f)) {
                items(moodList.reversed().takeLast(7).reversed()) { entry ->
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Row(){
                            Text("${entry.mood} ",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                "- ${formatDate(entry.timestamp)}",
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }

                        if (entry.notes?.isNotBlank() == true) {
                            Text("Note: ${entry.notes}", style = MaterialTheme.typography.bodySmall)
                        }
                        Text("Sleep: ${entry.sleepHours}, Stress: ${entry.stressLevel}", style = MaterialTheme.typography.bodySmall)
                        Divider(modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }
        }
        Button(
            onClick = { showHistory = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text("View Entire Mood History")

        }

        if (showHistory) {
            AlertDialog(
                modifier = Modifier.height(600.dp),
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

fun formatDate(timestamp: Long): String {
    return SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(timestamp))
}
