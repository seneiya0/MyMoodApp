package com.example.mymood.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
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
    val moodList = viewModel.moodEntries.collectAsState(initial = emptyList()).value

    val averageSleep = moodList.mapNotNull{ entry ->
        entry.sleepHours.filter {it.isDigit() }.toIntOrNull()}
        .average().takeIf {!it.isNaN() }?.let {String.format("%.1f", it)}?: "N/A"

    val averageStress = moodList.map{it.stressLevel}.average()
        .takeIf { !it.isNaN() }?.let{String.format("%.1f", it)}?: "N/A"

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Mood Summary", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(12.dp))

        Text("Average Sleep Hours: $averageSleep")
        Text("Average Stress Level: $averageStress")

        Spacer(modifier = Modifier.height(16.dp))
        Divider()
        Spacer(modifier = Modifier.height(16.dp))

        Text("Recent Mood Entries", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        if (moodList.isEmpty()) {
            Text("No mood entries yet.")
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(moodList.takeLast(7).reversed()) { entry ->
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Text("${formatDate(entry.timestamp)} - ${entry.mood}")
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
