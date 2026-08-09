package com.stivance.drawsync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
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

// ============================================================
// MAIN APP NAVIGATION
// ============================================================

@Composable
fun DrawSyncApp() {

    var currentScreen by remember {
        mutableStateOf("home")
    }

    // The drawing currently selected from My Drawings.
    var selectedDrawing by remember {
        mutableStateOf<SavedDrawing?>(null)
    }

    when (currentScreen) {

        // =====================================================
        // HOME
        // =====================================================

        "home" -> {

            HomeScreen(

                onStartDrawing = {

                    // New drawing = no selected drawing.
                    selectedDrawing = null

                    currentScreen =
                        "drawing"
                },

                onMyDrawings = {

                    currentScreen =
                        "my_drawings"
                }
            )
        }

        // =====================================================
        // DRAWING SCREEN
        // =====================================================

        "drawing" -> {

            // Android system Back.
            BackHandler {

                selectedDrawing = null

                currentScreen =
                    "home"
            }

            DrawingScreen(

                modifier =
                    Modifier.fillMaxSize(),

                onBack = {

                    selectedDrawing = null

                    currentScreen =
                        "home"
                },

                // If a saved drawing was selected,
                // send its pixels to the canvas.
                initialPixels =
                    selectedDrawing?.pixels
                        ?: emptySet(),

                existingDrawingId =
                    selectedDrawing?.id,

                existingDrawingName =
                    selectedDrawing?.name
            )
        }

        // =====================================================
        // MY DRAWINGS
        // =====================================================

        "my_drawings" -> {

            // Android system Back.
            BackHandler {

                currentScreen =
                    "home"
            }

            MyDrawingsScreen(

                onBack = {

                    currentScreen =
                        "home"
                },

                onOpenDrawing = { drawing ->

                    // Remember the selected drawing.
                    selectedDrawing =
                        drawing

                    // Open the drawing editor.
                    currentScreen =
                        "drawing"
                }
            )
        }
    }
}

// ============================================================
// HOME SCREEN
// ============================================================

@Composable
fun HomeScreen(
    onStartDrawing: () -> Unit,
    onMyDrawings: () -> Unit,
    modifier: Modifier = Modifier
) {

    Surface(

        modifier =
            modifier.fillMaxSize(),

        color =
            MaterialTheme
                .colorScheme
                .background
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 24.dp,
                    end = 24.dp,
                    top = 40.dp
                ),

            horizontalAlignment = Alignment.CenterHorizontally,

            verticalArrangement = Arrangement.Center
        ) {

            // -------------------------------------------------
            // APP NAME
            // -------------------------------------------------

            Text(

                text =
                    "DrawSync",

                style =
                    MaterialTheme
                        .typography
                        .displaySmall,

                fontWeight =
                    FontWeight.Bold,

                color =
                    MaterialTheme
                        .colorScheme
                        .onBackground
            )

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            // -------------------------------------------------
            // TAGLINE
            // -------------------------------------------------

            Text(

                text =
                    "Turn ideas into pixels.",

                style =
                    MaterialTheme
                        .typography
                        .titleLarge,

                fontWeight =
                    FontWeight.SemiBold,

                color =
                    MaterialTheme
                        .colorScheme
                        .primary,

                textAlign =
                    TextAlign.Center
            )

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            Text(

                text =
                    "Sketch it. Sync it. See it come alive.",

                style =
                    MaterialTheme
                        .typography
                        .bodyLarge,

                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant,

                textAlign =
                    TextAlign.Center
            )

            Spacer(
                modifier =
                    Modifier.height(36.dp)
            )

            // -------------------------------------------------
            // MAIN CARD
            // -------------------------------------------------

            Card(

                modifier =
                    Modifier.fillMaxWidth(),

                colors =
                    CardDefaults.cardColors(

                        containerColor =
                            MaterialTheme
                                .colorScheme
                                .surfaceVariant
                    ),

                elevation =
                    CardDefaults.cardElevation(

                        defaultElevation =
                            6.dp
                    )
            ) {

                Column(

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),

                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    // -----------------------------------------
                    // READY TO CREATE
                    // -----------------------------------------

                    Text(

                        text =
                            "Ready to Create?",

                        style =
                            MaterialTheme
                                .typography
                                .titleLarge,

                        fontWeight =
                            FontWeight.SemiBold,

                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurface
                    )

                    Spacer(
                        modifier =
                            Modifier.height(22.dp)
                    )

                    // -----------------------------------------
                    // START DRAWING
                    // -----------------------------------------

                    Button(

                        onClick =
                            onStartDrawing,

                        modifier =
                            Modifier.fillMaxWidth(),

                        colors =
                            ButtonDefaults.buttonColors(

                                containerColor =
                                    MaterialTheme
                                        .colorScheme
                                        .primary,

                                contentColor =
                                    Color.White
                            )
                    ) {

                        Text(

                            text =
                                "Start Drawing",

                            fontWeight =
                                FontWeight.SemiBold
                        )
                    }

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    // -----------------------------------------
                    // MY DRAWINGS
                    // -----------------------------------------

                    OutlinedButton(

                        onClick =
                            onMyDrawings,

                        modifier =
                            Modifier.fillMaxWidth()
                    ) {

                        Text(
                            "My Drawings"
                        )
                    }

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    // -----------------------------------------
                    // CONNECT OLED
                    // -----------------------------------------

                    OutlinedButton(

                        onClick = {

                            // Bluetooth will be
                            // implemented later.
                        },

                        modifier =
                            Modifier.fillMaxWidth()
                    ) {

                        Text(
                            "Connect OLED"
                        )
                    }
                }
            }

            Spacer(
                modifier =
                    Modifier.height(28.dp)
            )

            // -------------------------------------------------
            // FOOTER
            // -------------------------------------------------

            Text(

                text =
                    "BY STIVANCE",

                style =
                    MaterialTheme
                        .typography
                        .labelMedium,

                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant,

                textAlign =
                    TextAlign.Center
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

            onStartDrawing = {},

            onMyDrawings = {}
        )
    }
}