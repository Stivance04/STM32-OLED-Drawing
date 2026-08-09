package com.stivance.drawsync

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class SavedDrawing(
    val id: String,
    val name: String,
    val pixels: Set<Int>,
    val timestamp: Long
)

object DrawingStorage {

    private const val PREFS_NAME = "drawsync_drawings"
    private const val DRAWINGS_KEY = "saved_drawings"

    // =========================================================
    // GET SHARED PREFERENCES
    // =========================================================

    private fun getPreferences(
        context: Context
    ) = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    // =========================================================
    // SAVE DRAWING
    // =========================================================

    fun saveDrawing(
        context: Context,
        name: String,
        pixels: Set<Int>
    ) {

        val drawings =
            getAllDrawings(context).toMutableList()

        val now =
            System.currentTimeMillis()

        val drawing =
            SavedDrawing(
                id = now.toString(),
                name = name.trim(),
                pixels = pixels.toSet(),
                timestamp = now
            )

        drawings.add(drawing)

        saveAllDrawings(
            context,
            drawings
        )
    }

    // =========================================================
    // GET ALL DRAWINGS
    // =========================================================

    fun getAllDrawings(
        context: Context
    ): List<SavedDrawing> {

        val preferences =
            getPreferences(context)

        val jsonString =
            preferences.getString(
                DRAWINGS_KEY,
                null
            )
                ?: return emptyList()

        return try {

            val jsonArray =
                JSONArray(jsonString)

            val drawings =
                mutableListOf<SavedDrawing>()

            for (
            index in 0 until jsonArray.length()
            ) {

                val objectData =
                    jsonArray.getJSONObject(index)

                val id =
                    objectData.getString("id")

                val name =
                    objectData.getString("name")

                val timestamp =
                    objectData.getLong("timestamp")

                val pixelArray =
                    objectData.getJSONArray("pixels")

                val pixels =
                    mutableSetOf<Int>()

                for (
                pixelIndex in
                0 until pixelArray.length()
                ) {

                    pixels.add(
                        pixelArray.getInt(
                            pixelIndex
                        )
                    )
                }

                drawings.add(
                    SavedDrawing(
                        id = id,
                        name = name,
                        pixels = pixels,
                        timestamp = timestamp
                    )
                )
            }

            drawings.sortedByDescending {
                it.timestamp
            }

        } catch (
            exception: Exception
        ) {

            emptyList()
        }
    }

    // =========================================================
    // DELETE DRAWING
    // =========================================================

    fun deleteDrawing(
        context: Context,
        drawingId: String
    ) {

        val drawings =
            getAllDrawings(context)
                .filter {
                    it.id != drawingId
                }

        saveAllDrawings(
            context,
            drawings
        )
    }

    // =========================================================
    // GET ONE DRAWING
    // =========================================================

    fun getDrawing(
        context: Context,
        drawingId: String
    ): SavedDrawing? {

        return getAllDrawings(context)
            .find {
                it.id == drawingId
            }
    }

    // =========================================================
    // DELETE ALL DRAWINGS
    // =========================================================

    fun deleteAllDrawings(
        context: Context
    ) {

        getPreferences(context)
            .edit()
            .remove(DRAWINGS_KEY)
            .apply()
    }

    // =========================================================
    // INTERNAL SAVE FUNCTION
    // =========================================================

    private fun saveAllDrawings(
        context: Context,
        drawings: List<SavedDrawing>
    ) {

        val jsonArray =
            JSONArray()

        for (drawing in drawings) {

            val drawingObject =
                JSONObject()

            drawingObject.put(
                "id",
                drawing.id
            )

            drawingObject.put(
                "name",
                drawing.name
            )

            drawingObject.put(
                "timestamp",
                drawing.timestamp
            )

            val pixelArray =
                JSONArray()

            for (pixel in drawing.pixels) {

                pixelArray.put(pixel)
            }

            drawingObject.put(
                "pixels",
                pixelArray
            )

            drawingObject.put(
                "width",
                128
            )

            drawingObject.put(
                "height",
                64
            )

            jsonArray.put(
                drawingObject
            )
        }

        getPreferences(context)
            .edit()
            .putString(
                DRAWINGS_KEY,
                jsonArray.toString()
            )
            .apply()
    }
}