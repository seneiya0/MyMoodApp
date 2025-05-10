package com.example.mymood.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
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
            LazyColumn(modifier = Modifier.fillMaxSize()) {
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
    }
}

fun formatDate(timestamp: Long): String {
    return SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(timestamp))
}
