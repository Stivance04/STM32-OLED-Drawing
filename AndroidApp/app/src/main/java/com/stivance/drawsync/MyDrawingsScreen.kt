package com.stivance.drawsync

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

private const val PREVIEW_WIDTH = 128
private const val PREVIEW_HEIGHT = 64

@Composable
fun MyDrawingsScreen(
    onBack: () -> Unit,
    onOpenDrawing: (SavedDrawing) -> Unit
) {

    val context =
        androidx.compose.ui.platform.LocalContext.current

    var drawings by remember {
        mutableStateOf(
            DrawingStorage.getAllDrawings(context)
        )
    }

    var drawingToDelete by remember {
        mutableStateOf<SavedDrawing?>(null)
    }

    // =========================================================
    // RELOAD DRAWINGS
    // =========================================================

    LaunchedEffect(Unit) {

        drawings =
            DrawingStorage.getAllDrawings(context)
    }

    // =========================================================
    // MAIN SCREEN
    // =========================================================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0B0F))
            .padding(
                start = 16.dp,
                end = 16.dp,
                top = 24.dp,
                bottom = 16.dp
            )
    ) {

        // =====================================================
        // HEADER
        // =====================================================

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
        ) {

            // -------------------------------------------------
            // BACK BUTTON
            // -------------------------------------------------

            TextButton(
                onClick = onBack,
                modifier = Modifier.align(
                    Alignment.CenterStart
                )
            ) {

                Text("← Back")
            }

            // -------------------------------------------------
            // CENTERED HEADING
            // -------------------------------------------------

            Column(
                modifier = Modifier.align(
                    Alignment.Center
                ),

                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Text(
                    text = "My Drawings",

                    style =
                        MaterialTheme
                            .typography
                            .headlineMedium,

                    color = Color.White,

                    textAlign =
                        TextAlign.Center
                )

                Spacer(
                    modifier =
                        Modifier.height(2.dp)
                )

                Text(
                    text =
                        "${drawings.size} saved drawing" +
                                if (drawings.size == 1)
                                    ""
                                else
                                    "s",

                    style =
                        MaterialTheme
                            .typography
                            .bodySmall,

                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant,

                    textAlign =
                        TextAlign.Center
                )
            }
        }

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        // =====================================================
        // EMPTY STATE
        // =====================================================

        if (drawings.isEmpty()) {

            Box(
                modifier =
                    Modifier.fillMaxSize(),

                contentAlignment =
                    Alignment.Center
            ) {

                Column(
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "No drawings yet",

                        style =
                            MaterialTheme
                                .typography
                                .headlineSmall,

                        color = Color.White
                    )

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )

                    Text(
                        text =
                            "Create something on the canvas\n" +
                                    "and save it here.",

                        style =
                            MaterialTheme
                                .typography
                                .bodyMedium,

                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant,

                        textAlign =
                            TextAlign.Center
                    )
                }
            }

        } else {

            // =================================================
            // DRAWING LIST
            // =================================================

            LazyColumn(

                modifier =
                    Modifier.fillMaxSize(),

                verticalArrangement =
                    Arrangement.spacedBy(12.dp),

                contentPadding =
                    PaddingValues(
                        bottom = 20.dp
                    )
            ) {

                items(

                    items = drawings,

                    key = {
                        it.id
                    }

                ) { drawing ->

                    DrawingCard(

                        drawing = drawing,

                        onOpen = {
                            onOpenDrawing(drawing)
                        },

                        onDelete = {
                            drawingToDelete = drawing
                        }
                    )
                }
            }
        }
    }

    // =========================================================
    // DELETE CONFIRMATION
    // =========================================================

    drawingToDelete?.let { drawing ->

        AlertDialog(

            onDismissRequest = {
                drawingToDelete = null
            },

            title = {
                Text("Delete Drawing?")
            },

            text = {
                Text(
                    "Are you sure you want to delete " +
                            "\"${drawing.name}\"?"
                )
            },

            confirmButton = {

                TextButton(

                    onClick = {

                        DrawingStorage.deleteDrawing(
                            context,
                            drawing.id
                        )

                        drawings =
                            DrawingStorage
                                .getAllDrawings(context)

                        drawingToDelete = null
                    }
                ) {

                    Text("Delete")
                }
            },

            dismissButton = {

                TextButton(

                    onClick = {
                        drawingToDelete = null
                    }
                ) {

                    Text("Cancel")
                }
            }
        )
    }
}

// ============================================================
// DRAWING CARD
// ============================================================

@Composable
private fun DrawingCard(
    drawing: SavedDrawing,
    onOpen: () -> Unit,
    onDelete: () -> Unit
) {

    Card(

        modifier =
            Modifier.fillMaxWidth(),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color(0xFF17171D)
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 3.dp
            )
    ) {

        Row(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            // =================================================
            // DRAWING PREVIEW
            // =================================================

            DrawingPreview(

                pixels =
                    drawing.pixels,

                modifier =
                    Modifier
                        .width(110.dp)
                        .aspectRatio(2f)
            )

            Spacer(
                modifier =
                    Modifier.width(14.dp)
            )

            // =================================================
            // DRAWING INFORMATION
            // =================================================

            Column(
                modifier =
                    Modifier.weight(1f)
            ) {

                Text(
                    text =
                        drawing.name,

                    style =
                        MaterialTheme
                            .typography
                            .titleMedium,

                    color = Color.White
                )

                Spacer(
                    modifier =
                        Modifier.height(5.dp)
                )

                Text(
                    text =
                        "128 × 64 OLED",

                    style =
                        MaterialTheme
                            .typography
                            .bodySmall,

                    color =
                        MaterialTheme
                            .colorScheme
                            .primary
                )

                Spacer(
                    modifier =
                        Modifier.height(2.dp)
                )

                Text(
                    text =
                        "${drawing.pixels.size} pixels",

                    style =
                        MaterialTheme
                            .typography
                            .bodySmall,

                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant
                )

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                // =================================================
                // ACTIONS
                // =================================================

                Row(
                    horizontalArrangement =
                        Arrangement.spacedBy(6.dp)
                ) {

                    Button(

                        onClick = onOpen,

                        contentPadding =
                            PaddingValues(
                                horizontal = 14.dp
                            ),

                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor =
                                    MaterialTheme
                                        .colorScheme
                                        .primary
                            )
                    ) {

                        Text("Open")
                    }

                    TextButton(
                        onClick = onDelete
                    ) {

                        Text("Delete")
                    }
                }
            }
        }
    }
}

// ============================================================
// DRAWING PREVIEW
// ============================================================

@Composable
private fun DrawingPreview(
    pixels: Set<Int>,
    modifier: Modifier = Modifier
) {

    Box(

        modifier =
            modifier
                .background(Color.Black)
                .border(
                    width = 1.dp,
                    color = Color(0xFF44444D)
                )
    ) {

        Canvas(
            modifier =
                Modifier.fillMaxSize()
        ) {

            val pixelWidth =
                size.width /
                        PREVIEW_WIDTH

            val pixelHeight =
                size.height /
                        PREVIEW_HEIGHT

            for (id in pixels) {

                val x =
                    id % PREVIEW_WIDTH

                val y =
                    id / PREVIEW_WIDTH

                drawRect(

                    color = Color.White,

                    topLeft =
                        androidx.compose.ui.geometry
                            .Offset(
                                x * pixelWidth,
                                y * pixelHeight
                            ),

                    size =
                        androidx.compose.ui.geometry
                            .Size(
                                pixelWidth,
                                pixelHeight
                            )
                )
            }
        }
    }
}