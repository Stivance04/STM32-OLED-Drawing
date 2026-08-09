package com.stivance.drawsync

object OledDataEncoder {

    const val WIDTH = 128
    const val HEIGHT = 64

    // 128 × 64 monochrome pixels
    // 8 pixels are packed into one byte.
    const val FRAME_SIZE = (WIDTH * HEIGHT) / 8

    /**
     * Converts the saved pixel set into a
     * compact 1024-byte OLED frame.
     *
     * Pixel ID format:
     *
     * id = y * 128 + x
     *
     * Each bit represents one OLED pixel.
     */
    fun encode(
        pixels: Set<Int>
    ): ByteArray {

        val frame =
            ByteArray(FRAME_SIZE)

        for (pixelId in pixels) {

            if (
                pixelId < 0 ||
                pixelId >= WIDTH * HEIGHT
            ) {
                continue
            }

            val x =
                pixelId % WIDTH

            val y =
                pixelId / WIDTH

            /*
             * SSD1306-style page layout:
             *
             * Each byte contains 8 vertical pixels.
             *
             * byte index:
             *
             * x + (y / 8) * WIDTH
             *
             * bit:
             *
             * y % 8
             */

            val byteIndex =
                x + (y / 8) * WIDTH

            val bit =
                y % 8

            frame[byteIndex] =
                (
                        frame[byteIndex].toInt()
                            .and(0xFF)
                            .or(1 shl bit)
                        ).toByte()
        }

        return frame
    }

    /**
     * Converts a byte value into an unsigned
     * integer from 0 to 255.
     */
    fun unsignedByte(
        value: Byte
    ): Int {

        return value.toInt() and 0xFF
    }
}