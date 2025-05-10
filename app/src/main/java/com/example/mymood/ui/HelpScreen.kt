package com.example.mymood.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mymood.ui.theme.MyMoodTheme

@Composable
fun HelpScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Help & Tips",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "⭐ How to Use MyMood:",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "- On the Home screen, select an emoji that best matches your mood.\n" +
                    "😊 for happy, 😐 for neutral, 🙁 for upset, or❓ for other\n" +
                    "- Add an optional note to describe your day or feelings.\n" +
                    "- Choose your sleep hours and stress level.\n" +
                    "- Tap 'Save Mood' to log your entry.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "⏰ Reminders:",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "- Set a daily reminder time under Preferences.\n" +
                    "- Notifications will prompt you to log your mood at your chosen time.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "📊 Mood Summary:",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "- View your recent mood history, average sleep hours, and stress level.\n" +
                    "- See trends over time to reflect on your mental health habits.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Need more help?\nFeel free to reach out for support! \nexample@email.com",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}


@Preview(showBackground = true)
@Composable
fun HelpScreenPreview(){
    MyMoodTheme {
        HelpScreen(navController = rememberNavController())
    }

}