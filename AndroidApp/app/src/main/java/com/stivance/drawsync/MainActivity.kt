package com.stivance.drawsync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stivance.drawsync.ui.theme.DrawSyncTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DrawSyncTheme {
                DrawSyncApp()
            }
        }
    }
}

@Composable
fun DrawSyncApp() {

    var showDrawingScreen by remember {
        mutableStateOf(false)
    }

    if (showDrawingScreen) {

        DrawingScreen(
            modifier = Modifier.fillMaxSize()
        )

    } else {

        HomeScreen(
            onStartDrawing = {
                showDrawingScreen = true
            }
        )
    }
}


// ============================================================
// HOME SCREEN
// ============================================================

@Composable
fun HomeScreen(
    onStartDrawing: () -> Unit,
    modifier: Modifier = Modifier
) {

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),

            horizontalAlignment = Alignment.CenterHorizontally,

            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "DrawSync",

                style = MaterialTheme.typography.displaySmall,

                fontWeight = FontWeight.Bold,

                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = "Turn ideas into pixels.",

                style = MaterialTheme.typography.titleLarge,

                fontWeight = FontWeight.SemiBold,

                color = MaterialTheme.colorScheme.primary,

                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Sketch it. Sync it. See it come alive.",

                style = MaterialTheme.typography.bodyLarge,

                color = MaterialTheme.colorScheme.onSurfaceVariant,

                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(36.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),

                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),

                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                )
            ) {

                Column(

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),

                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "Ready to Create?",

                        style = MaterialTheme.typography.titleLarge,

                        fontWeight = FontWeight.SemiBold,

                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(
                        modifier = Modifier.height(22.dp)
                    )

                    Button(

                        onClick = onStartDrawing,

                        modifier = Modifier.fillMaxWidth(),

                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,

                            contentColor = Color.White
                        )
                    ) {

                        Text(
                            text = "Start Drawing",

                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    OutlinedButton(

                        onClick = {
                            // Saved drawings will be implemented later
                        },

                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Text("My Drawings")
                    }

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    OutlinedButton(

                        onClick = {
                            // Bluetooth will be implemented later
                        },

                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Text("Connect OLED")
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(28.dp)
            )

            Text(
                text = "BY STIVANCE",

                style = MaterialTheme.typography.labelMedium,

                color = MaterialTheme.colorScheme.onSurfaceVariant,

                textAlign = TextAlign.Center
            )
        }
    }
}


// ============================================================
// PREVIEW
// ============================================================

@Preview(
    showBackground = false,
    showSystemUi = true
)
@Composable
fun HomeScreenPreview() {

    DrawSyncTheme {

        HomeScreen(
            onStartDrawing = {}
        )
    }
}