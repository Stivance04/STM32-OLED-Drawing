package com.stivance.drawsync

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.floor

private const val OLED_WIDTH = 128
private const val OLED_HEIGHT = 64

@Composable
fun DrawingScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    initialPixels: Set<Int> = emptySet(),
    existingDrawingId: String? = null,
    existingDrawingName: String? = null
) {

    val context = LocalContext.current

    // =========================================================
    // ACTIVE PIXELS
    // =========================================================

    val activePixels = remember {
        mutableStateSetOf<Int>()
    }

    // =========================================================
    // LOAD SAVED DRAWING
    // =========================================================

    LaunchedEffect(existingDrawingId) {

        activePixels.clear()

        activePixels.addAll(
            initialPixels
        )
    }

    // =========================================================
    // UNDO / REDO
    // =========================================================

    val undoHistory = remember {
        mutableListOf<Set<Int>>()
    }

    val redoHistory = remember {
        mutableListOf<Set<Int>>()
    }

    // =========================================================
    // TOOL SETTINGS
    // =========================================================

    var eraserEnabled by remember {
        mutableStateOf(false)
    }

    var brushSize by remember {
        mutableStateOf(1)
    }

    // =========================================================
    // SAVE DIALOG
    // =========================================================

    var showSaveDialog by remember {
        mutableStateOf(false)
    }

    var drawingName by remember {
        mutableStateOf(
            existingDrawingName ?: ""
        )
    }

    // =========================================================
    // SAVE UNDO STATE
    // =========================================================

    fun saveUndoState() {

        undoHistory.add(
            activePixels.toSet()
        )

        if (undoHistory.size > 30) {
            undoHistory.removeAt(0)
        }

        redoHistory.clear()
    }

    // =========================================================
    // PIXEL ID
    // =========================================================

    fun pixelId(
        x: Int,
        y: Int
    ): Int {

        return y * OLED_WIDTH + x
    }

    // =========================================================
    // APPLY PIXEL
    // =========================================================

    fun applyPixel(
        x: Int,
        y: Int
    ) {

        if (
            x !in 0 until OLED_WIDTH ||
            y !in 0 until OLED_HEIGHT
        ) {
            return
        }

        val startOffset =
            (brushSize - 1) / 2

        for (offsetY in 0 until brushSize) {

            for (offsetX in 0 until brushSize) {

                val px =
                    x + offsetX - startOffset

                val py =
                    y + offsetY - startOffset

                if (
                    px in 0 until OLED_WIDTH &&
                    py in 0 until OLED_HEIGHT
                ) {

                    val id =
                        pixelId(px, py)

                    if (eraserEnabled) {

                        activePixels.remove(id)

                    } else {

                        activePixels.add(id)
                    }
                }
            }
        }
    }

    // =========================================================
    // DRAW CONTINUOUS LINE
    // =========================================================

    fun drawLine(
        startX: Int,
        startY: Int,
        endX: Int,
        endY: Int
    ) {

        var x = startX
        var y = startY

        val dx =
            abs(endX - startX)

        val dy =
            abs(endY - startY)

        val stepX =
            if (startX < endX) 1 else -1

        val stepY =
            if (startY < endY) 1 else -1

        var error =
            dx - dy

        while (true) {

            applyPixel(
                x,
                y
            )

            if (
                x == endX &&
                y == endY
            ) {
                break
            }

            val error2 =
                error * 2

            if (error2 > -dy) {

                error -= dy
                x += stepX
            }

            if (error2 < dx) {

                error += dx
                y += stepY
            }
        }
    }

    // =========================================================
    // MAIN SCREEN
    // =========================================================

    Column(

        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0B0B0F))
            .padding(
                start = 16.dp,
                end = 16.dp,
                top = 24.dp,
                bottom = 16.dp
            ),

        horizontalAlignment =
            Alignment.CenterHorizontally
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

                modifier =
                    Modifier.align(
                        Alignment.CenterStart
                    )
            ) {

                Text("← Back")
            }

            // -------------------------------------------------
            // CENTERED TITLE
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

                    text =
                        existingDrawingName
                            ?: "Create",

                    style =
                        MaterialTheme
                            .typography
                            .headlineMedium,

                    color =
                        Color.White,

                    textAlign =
                        TextAlign.Center
                )

                Spacer(
                    modifier =
                        Modifier.height(2.dp)
                )

                Text(

                    text =
                        "128 × 64 OLED",

                    style =
                        MaterialTheme
                            .typography
                            .labelMedium,

                    color =
                        MaterialTheme
                            .colorScheme
                            .primary,

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
        // OLED CANVAS
        // =====================================================

        Box(

            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f)
                .border(
                    width = 2.dp,
                    color = Color(0xFF44444D)
                )
                .background(Color.Black)
        ) {

            Canvas(

                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(
                        eraserEnabled,
                        brushSize
                    ) {

                        var lastX = 0
                        var lastY = 0

                        detectDragGestures(

                            // ---------------------------------
                            // START DRAWING
                            // ---------------------------------

                            onDragStart = { position ->

                                val pixelWidth =
                                    size.width /
                                            OLED_WIDTH.toFloat()

                                val pixelHeight =
                                    size.height /
                                            OLED_HEIGHT.toFloat()

                                val x =
                                    floor(
                                        position.x /
                                                pixelWidth
                                    ).toInt()

                                val y =
                                    floor(
                                        position.y /
                                                pixelHeight
                                    ).toInt()

                                lastX = x
                                lastY = y

                                saveUndoState()

                                applyPixel(
                                    x,
                                    y
                                )
                            },

                            // ---------------------------------
                            // CONTINUE DRAWING
                            // ---------------------------------

                            onDrag = { change, _ ->

                                change.consume()

                                val pixelWidth =
                                    size.width /
                                            OLED_WIDTH.toFloat()

                                val pixelHeight =
                                    size.height /
                                            OLED_HEIGHT.toFloat()

                                val x =
                                    floor(
                                        change.position.x /
                                                pixelWidth
                                    ).toInt()

                                val y =
                                    floor(
                                        change.position.y /
                                                pixelHeight
                                    ).toInt()

                                drawLine(

                                    startX = lastX,
                                    startY = lastY,

                                    endX = x,
                                    endY = y
                                )

                                lastX = x
                                lastY = y
                            }
                        )
                    }
            ) {

                val pixelWidth =
                    size.width /
                            OLED_WIDTH

                val pixelHeight =
                    size.height /
                            OLED_HEIGHT

                // ---------------------------------------------
                // DRAW ACTIVE PIXELS
                // ---------------------------------------------

                for (id in activePixels) {

                    val x =
                        id % OLED_WIDTH

                    val y =
                        id / OLED_WIDTH

                    drawRect(

                        color =
                            Color.White,

                        topLeft =
                            Offset(
                                x * pixelWidth,
                                y * pixelHeight
                            ),

                        size =
                            Size(
                                pixelWidth,
                                pixelHeight
                            )
                    )
                }
            }
        }

        Spacer(
            modifier =
                Modifier.height(8.dp)
        )

        // =====================================================
        // PIXEL COUNTER
        // =====================================================

        Text(

            text =
                "${activePixels.size} / 8192 pixels",

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
                Modifier.height(10.dp)
        )

        // =====================================================
        // UNDO / REDO / ERASER
        // =====================================================

        Row(

            modifier =
                Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.spacedBy(8.dp),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Button(

                modifier =
                    Modifier.weight(1f),

                enabled =
                    undoHistory.isNotEmpty(),

                onClick = {

                    if (
                        undoHistory.isNotEmpty()
                    ) {

                        redoHistory.add(
                            activePixels.toSet()
                        )

                        val previous =
                            undoHistory.removeAt(
                                undoHistory.lastIndex
                            )

                        activePixels.clear()

                        activePixels.addAll(
                            previous
                        )
                    }
                }
            ) {

                Text("↩ Undo")
            }

            Button(

                modifier =
                    Modifier.weight(1f),

                enabled =
                    redoHistory.isNotEmpty(),

                onClick = {

                    if (
                        redoHistory.isNotEmpty()
                    ) {

                        undoHistory.add(
                            activePixels.toSet()
                        )

                        val next =
                            redoHistory.removeAt(
                                redoHistory.lastIndex
                            )

                        activePixels.clear()

                        activePixels.addAll(
                            next
                        )
                    }
                }
            ) {

                Text("↪ Redo")
            }

            Button(

                modifier =
                    Modifier.weight(1f),

                onClick = {

                    eraserEnabled =
                        !eraserEnabled
                }
            ) {

                Text(

                    if (eraserEnabled)
                        "Eraser ✓"
                    else
                        "Eraser"
                )
            }
        }

        Spacer(
            modifier =
                Modifier.height(10.dp)
        )

        // =====================================================
        // BRUSH SIZE
        // =====================================================

        Text(

            text =
                "Brush Size: $brushSize px",

            style =
                MaterialTheme
                    .typography
                    .bodyMedium,

            color =
                Color.White
        )

        Slider(

            value =
                brushSize.toFloat(),

            onValueChange = {

                brushSize =
                    it.toInt()
                        .coerceIn(1, 4)
            },

            valueRange =
                1f..4f,

            steps =
                2,

            modifier =
                Modifier.fillMaxWidth()
        )

        Spacer(
            modifier =
                Modifier.height(4.dp)
        )

        // =====================================================
        // CLEAR DRAWING
        // =====================================================

        Button(

            enabled =
                activePixels.isNotEmpty(),

            onClick = {

                if (
                    activePixels.isNotEmpty()
                ) {

                    saveUndoState()

                    activePixels.clear()
                }
            }
        ) {

            Text("Clear Drawing")
        }

        Spacer(
            modifier =
                Modifier.height(8.dp)
        )

        // =====================================================
        // SAVE DRAWING
        // =====================================================

        Button(

            enabled =
                activePixels.isNotEmpty(),

            onClick = {

                drawingName =
                    existingDrawingName ?: ""

                showSaveDialog =
                    true
            }
        ) {

            Text(

                if (existingDrawingId != null)
                    "Save Changes"
                else
                    "Save Drawing"
            )
        }

        // =====================================================
        // SAVE DIALOG
        // =====================================================

        if (showSaveDialog) {

            AlertDialog(

                onDismissRequest = {

                    showSaveDialog =
                        false
                },

                title = {

                    Text(

                        if (existingDrawingId != null)
                            "Save Changes"
                        else
                            "Save Drawing"
                    )
                },

                text = {

                    OutlinedTextField(

                        value =
                            drawingName,

                        onValueChange = {

                            drawingName =
                                it
                        },

                        singleLine = true,

                        label = {

                            Text(
                                "Drawing name"
                            )
                        }
                    )
                },

                confirmButton = {

                    TextButton(

                        enabled =
                            drawingName
                                .trim()
                                .isNotEmpty(),

                        onClick = {

                            val cleanName =
                                drawingName.trim()

                            // ---------------------------------
                            // UPDATE EXISTING DRAWING
                            // ---------------------------------

                            if (
                                existingDrawingId != null
                            ) {

                                DrawingStorage
                                    .deleteDrawing(

                                        context =
                                            context,

                                        drawingId =
                                            existingDrawingId
                                    )
                            }

                            // ---------------------------------
                            // SAVE DRAWING
                            // ---------------------------------

                            DrawingStorage.saveDrawing(

                                context =
                                    context,

                                name =
                                    cleanName,

                                pixels =
                                    activePixels.toSet()
                            )

                            showSaveDialog =
                                false
                        }
                    ) {

                        Text("Save")
                    }
                },

                dismissButton = {

                    TextButton(

                        onClick = {

                            showSaveDialog =
                                false
                        }
                    ) {

                        Text("Cancel")
                    }
                }
            )
        }
    }
}