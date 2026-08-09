package com.stivance.drawsync

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

class BluetoothManager(
    private val context: Context
) {

    private val bluetoothAdapter: BluetoothAdapter? =
        BluetoothAdapter.getDefaultAdapter()

    // =========================================================
    // BLUETOOTH SUPPORT
    // =========================================================

    fun isBluetoothSupported(): Boolean {
        return bluetoothAdapter != null
    }

    // =========================================================
    // BLUETOOTH PERMISSION
    // =========================================================

    private fun hasBluetoothPermission(): Boolean {

        return android.os.Build.VERSION.SDK_INT < 31 ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) == PackageManager.PERMISSION_GRANTED
    }

    // =========================================================
    // BLUETOOTH ENABLED
    // =========================================================

    fun isBluetoothEnabled(): Boolean {

        if (!hasBluetoothPermission()) {
            return false
        }

        return bluetoothAdapter?.isEnabled == true
    }

    // =========================================================
    // GET STATUS
    // =========================================================

    fun getStatus(): BluetoothStatus {

        if (!isBluetoothSupported()) {

            return BluetoothStatus.NOT_SUPPORTED
        }

        if (!hasBluetoothPermission()) {

            return BluetoothStatus.PERMISSION_REQUIRED
        }

        if (!isBluetoothEnabled()) {

            return BluetoothStatus.BLUETOOTH_OFF
        }

        return BluetoothStatus.NOT_CONNECTED
    }
}

// =============================================================
// BLUETOOTH STATUS
// =============================================================

enum class BluetoothStatus {

    NOT_SUPPORTED,

    PERMISSION_REQUIRED,

    BLUETOOTH_OFF,

    NOT_CONNECTED,

    CONNECTED
}