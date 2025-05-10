package com.example.mymood.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.mymood.viewmodel.PreferencesViewModel
import com.example.mymood.worker.WorkManagerScheduler
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferencesScreen(
    navController: NavController,
    viewModel: PreferencesViewModel,
    snackbarHostState: SnackbarHostState
) {
    val defaultMood by viewModel.defaultMood.collectAsState()
    val defaultSleep by viewModel.defaultSleep.collectAsState()
    val reminderTime by viewModel.reminderTime.collectAsState()

    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            Toast.makeText(context, "Notification permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    var moodInput by remember { mutableStateOf(defaultMood) }
    var sleepInput by remember { mutableStateOf(defaultSleep) }
    var timeInput by remember { mutableStateOf(reminderTime) }

    val moodOptions = listOf("😊", "😐", "😢")
    val sleepOptions = listOf("5 hours", "6 hours", "7 hours", "8 hours", "9 hours", "10 hours")
    val reminderOptions = listOf("8:00 AM", "9:00 AM", "10:00 AM", "12:00 PM", "3:00 PM", "6:00 PM")

    var moodDropdownExpanded by remember { mutableStateOf(false) }
    var sleepDropdownExpanded by remember { mutableStateOf(false) }
    var timeDropdownExpanded by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("Preferences", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = moodDropdownExpanded,
                onExpandedChange = { moodDropdownExpanded = !moodDropdownExpanded }
            ) {
                TextField(
                    value = moodInput,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Default Mood") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(moodDropdownExpanded) },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.tertiary,
                        focusedContainerColor = MaterialTheme.colorScheme.secondary
                        ),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = moodDropdownExpanded,
                    onDismissRequest = { moodDropdownExpanded = false }
                ) {
                    moodOptions.forEach { mood ->
                        DropdownMenuItem(
                            text = { Text(mood) },
                            onClick = {
                                moodInput = mood
                                moodDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = sleepDropdownExpanded,
                onExpandedChange = { sleepDropdownExpanded = !sleepDropdownExpanded }
            ) {
                TextField(
                    value = sleepInput,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Sleep Goal") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(sleepDropdownExpanded) },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.tertiary,
                        focusedContainerColor = MaterialTheme.colorScheme.secondary
                    ),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = sleepDropdownExpanded,
                    onDismissRequest = { sleepDropdownExpanded = false }
                ) {
                    sleepOptions.forEach { sleep ->
                        DropdownMenuItem(
                            text = { Text(sleep) },
                            onClick = {
                                sleepInput = sleep
                                sleepDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = timeDropdownExpanded,
                onExpandedChange = { timeDropdownExpanded = !timeDropdownExpanded }
            ) {
                TextField(
                    value = timeInput,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Reminder Time") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(timeDropdownExpanded) },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.tertiary,
                        focusedContainerColor = MaterialTheme.colorScheme.secondary
                    ),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = timeDropdownExpanded,
                    onDismissRequest = { timeDropdownExpanded = false }
                ) {
                    reminderOptions.forEach { time ->
                        DropdownMenuItem(
                            text = { Text(time) },
                            onClick = {
                                timeInput = time
                                timeDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.setDefaultMood(moodInput)
                    viewModel.setDefaultSleep(sleepInput)
                    viewModel.setReminderTime(timeInput)

                    val parts = timeInput.split(":")
                    val hour = parts[0].toIntOrNull() ?: 20
                    val minute = parts.getOrNull(1)?.filter { it.isDigit() }?.toIntOrNull() ?: 0

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            return@Button
                        }
                    }

                    WorkManagerScheduler.scheduleReminderWorker(
                        context = context,
                        hour = hour,
                        minute = minute
                    )

                    scope.launch {
                        snackbarHostState.showSnackbar("Preferences saved!")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Preferences")
            }
        }
    }
}