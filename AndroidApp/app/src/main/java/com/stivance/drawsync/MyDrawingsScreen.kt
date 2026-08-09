package com.stivance.drawsync

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

private const val PREVIEW_WIDTH = 128
private const val PREVIEW_HEIGHT = 64

@Composable
fun MyDrawingsScreen(
    onBack: () -> Unit,
    onOpenDrawing: (SavedDrawing) -> Unit
) {

    val context = LocalContext.current

    // =========================================================
    // DRAWINGS
    // =========================================================

    var drawings by remember {
        mutableStateOf(
            DrawingStorage.getAllDrawings(context)
        )
    }

    var drawingToDelete by remember {
        mutableStateOf<SavedDrawing?>(null)
    }

    // =========================================================
    // BLUETOOTH MANAGER
    // =========================================================

    val bluetoothManager = remember {
        BluetoothManager(context)
    }

    var bluetoothStatus by remember {
        mutableStateOf(
            bluetoothManager.getStatus()
        )
    }

    // =========================================================
    // DRAWING WAITING TO BE SENT
    // =========================================================

    var drawingWaitingForPermission by remember {
        mutableStateOf<SavedDrawing?>(null)
    }

    // =========================================================
    // BLUETOOTH PERMISSION LAUNCHER
    // =========================================================

    val bluetoothPermissionLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.RequestPermission()
        ) { granted ->

            val drawing =
                drawingWaitingForPermission

            drawingWaitingForPermission = null

            if (granted) {

                bluetoothStatus =
                    bluetoothManager.getStatus()

                if (
                    drawing != null
                ) {

                    prepareDrawingForOled(
                        context = context,
                        drawing = drawing,
                        bluetoothManager =
                            bluetoothManager
                    )
                }

            } else {

                bluetoothStatus =
                    bluetoothManager.getStatus()

                Toast.makeText(
                    context,
                    "Bluetooth permission is required for OLED communication.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    // =========================================================
    // REQUEST BLUETOOTH PERMISSION
    // =========================================================

    fun sendDrawingToOled(
        drawing: SavedDrawing
    ) {

        /*
         * Android 12 and newer require
         * BLUETOOTH_CONNECT as a runtime permission.
         *
         * Android versions below 12 do not require
         * this runtime permission.
         */

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.S
        ) {

            val permission =
                Manifest.permission.BLUETOOTH_CONNECT

            val permissionGranted =
                androidx.core.content.ContextCompat
                    .checkSelfPermission(
                        context,
                        permission
                    ) ==
                        android.content.pm.PackageManager
                            .PERMISSION_GRANTED

            if (!permissionGranted) {

                drawingWaitingForPermission =
                    drawing

                bluetoothPermissionLauncher.launch(
                    permission
                )

                return
            }
        }

        // Permission already available.

        bluetoothStatus =
            bluetoothManager.getStatus()

        prepareDrawingForOled(
            context = context,
            drawing = drawing,
            bluetoothManager =
                bluetoothManager
        )
    }

    // =========================================================
    // RELOAD DRAWINGS
    // =========================================================

    LaunchedEffect(Unit) {

        drawings =
            DrawingStorage.getAllDrawings(context)

        bluetoothStatus =
            bluetoothManager.getStatus()
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
                .height(82.dp)
        ) {

            // -------------------------------------------------
            // BACK BUTTON
            // -------------------------------------------------

            TextButton(

                onClick = onBack,

                modifier =
                    Modifier.align(
                        Alignment.CenterStart
                    )
            ) {

                Text("← Back")
            }

            // -------------------------------------------------
            // CENTERED HEADING
            // -------------------------------------------------

            Column(

                modifier =
                    Modifier.align(
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

                Spacer(
                    modifier =
                        Modifier.height(3.dp)
                )

                BluetoothStatusText(
                    status = bluetoothStatus
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

                        text =
                            "No drawings yet",

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

                            onOpenDrawing(
                                drawing
                            )
                        },

                        onSendToOled = {

                            sendDrawingToOled(
                                drawing
                            )
                        },

                        onDelete = {

                            drawingToDelete =
                                drawing
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

                Text(
                    "Delete Drawing?"
                )
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

// =============================================================
// PREPARE DRAWING FOR OLED
// =============================================================

private fun prepareDrawingForOled(
    context: android.content.Context,
    drawing: SavedDrawing,
    bluetoothManager: BluetoothManager
) {

    val status =
        bluetoothManager.getStatus()

    when (status) {

        BluetoothStatus.NOT_SUPPORTED -> {

            Toast.makeText(
                context,
                "Bluetooth is not supported on this device.",
                Toast.LENGTH_LONG
            ).show()
        }

        BluetoothStatus.PERMISSION_REQUIRED -> {

            Toast.makeText(
                context,
                "Bluetooth permission is required.",
                Toast.LENGTH_LONG
            ).show()
        }

        BluetoothStatus.BLUETOOTH_OFF -> {

            Toast.makeText(
                context,
                "Please turn on Bluetooth first.",
                Toast.LENGTH_LONG
            ).show()
        }

        BluetoothStatus.NOT_CONNECTED -> {

            /*
             * We don't have the STM32/Bluetooth hardware yet.
             *
             * We can still generate the OLED frame.
             */

            val oledFrame =
                OledDataEncoder.encode(
                    drawing.pixels
                )

            Toast.makeText(
                context,
                "OLED frame ready: ${oledFrame.size} bytes. No OLED connected.",
                Toast.LENGTH_LONG
            ).show()
        }

        BluetoothStatus.CONNECTED -> {

            /*
             * Actual Bluetooth transmission will be
             * implemented after we choose the hardware.
             */

            val oledFrame =
                OledDataEncoder.encode(
                    drawing.pixels
                )

            Toast.makeText(
                context,
                "OLED frame ready: ${oledFrame.size} bytes.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}

// =============================================================
// BLUETOOTH STATUS
// =============================================================

@Composable
private fun BluetoothStatusText(
    status: BluetoothStatus
) {

    val text: String
    val statusColor: Color

    when (status) {

        BluetoothStatus.NOT_SUPPORTED -> {

            text =
                "● Bluetooth not supported"

            statusColor =
                MaterialTheme
                    .colorScheme
                    .error
        }

        BluetoothStatus.PERMISSION_REQUIRED -> {

            text =
                "● Bluetooth permission required"

            statusColor =
                MaterialTheme
                    .colorScheme
                    .error
        }

        BluetoothStatus.BLUETOOTH_OFF -> {

            text =
                "● Bluetooth is off"

            statusColor =
                MaterialTheme
                    .colorScheme
                    .error
        }

        BluetoothStatus.NOT_CONNECTED -> {

            text =
                "● OLED not connected"

            statusColor =
                MaterialTheme
                    .colorScheme
                    .onSurfaceVariant
        }

        BluetoothStatus.CONNECTED -> {

            text =
                "● OLED connected"

            statusColor =
                MaterialTheme
                    .colorScheme
                    .primary
        }
    }

    Text(

        text = text,

        style =
            MaterialTheme
                .typography
                .labelSmall,

        color =
            statusColor,

        textAlign =
            TextAlign.Center
    )
}

// =============================================================
// DRAWING CARD
// =============================================================

@Composable
private fun DrawingCard(

    drawing: SavedDrawing,

    onOpen: () -> Unit,

    onSendToOled: () -> Unit,

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

        Column(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
        ) {

            // =================================================
            // PREVIEW + INFORMATION
            // =================================================

            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

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

                        color =
                            Color.White
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
                }
            }

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            // =================================================
            // THREE EQUAL BUTTONS
            // =================================================

            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.spacedBy(8.dp),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Button(

                    onClick =
                        onOpen,

                    modifier =
                        Modifier.weight(1f),

                    contentPadding =
                        PaddingValues(
                            horizontal = 4.dp
                        )
                ) {

                    Text(
                        text = "Open",
                        maxLines = 1
                    )
                }

                Button(

                    onClick =
                        onSendToOled,

                    modifier =
                        Modifier.weight(1f),

                    colors =
                        ButtonDefaults.buttonColors(

                            containerColor =
                                MaterialTheme
                                    .colorScheme
                                    .secondary
                        ),

                    contentPadding =
                        PaddingValues(
                            horizontal = 4.dp
                        )
                ) {

                    Text(
                        text = "Send to OLED",
                        maxLines = 1
                    )
                }

                Button(

                    onClick =
                        onDelete,

                    modifier =
                        Modifier.weight(1f),

                    colors =
                        ButtonDefaults.buttonColors(

                            containerColor =
                                MaterialTheme
                                    .colorScheme
                                    .error
                        ),

                    contentPadding =
                        PaddingValues(
                            horizontal = 4.dp
                        )
                ) {

                    Text(
                        text = "Delete",
                        maxLines = 1
                    )
                }
            }
        }
    }
}

// =============================================================
// DRAWING PREVIEW
// =============================================================

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

                    color =
                        Color.White,

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