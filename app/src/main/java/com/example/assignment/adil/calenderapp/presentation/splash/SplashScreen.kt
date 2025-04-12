package com.example.assignment.adil.calenderapp.presentation.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.assignment.adil.calenderapp.R
import com.example.assignment.adil.calenderapp.ui.theme.CalenderAppTheme

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    onCancelSync: () -> Unit = {},
    syncStatus: String = "Syncing Data..."
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Sync,
                contentDescription = "Sync",
                modifier = Modifier.size(120.dp).padding(24.dp)
            )


            Text(
                text = syncStatus,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onCancelSync,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Cancel Sync")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    CalenderAppTheme {
        SplashScreen(
            syncStatus = "Syncing Tasks..."
        )
    }
} 