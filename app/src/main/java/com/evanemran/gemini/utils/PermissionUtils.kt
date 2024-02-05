package com.evanemran.gemini.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat

object PermissionUtils {

    const val PERMISSION_READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE"
    const val PERMISSION_CAMERA = "android.permission.CAMERA"
    const val PERMISSION_RECORD_AUDIO = "android.permission.RECORD_AUDIO"

    fun requestPermission(context: Activity, permission: String, rationale: String? = null, onGranted: () -> Unit) {
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            onGranted()
            return
        }

        // Explain why the permission is needed if a rationale is provided
        if (rationale != null && shouldShowRequestPermissionRationale(context, permission)) {
            AlertDialog.Builder(context)
                .setMessage(rationale)
                .setPositiveButton("Request Permission") { _, _ ->
                    requestPermissions(context, permission)
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
            return
        }

        // Request the permission directly
        requestPermissions(context, permission)
    }

    private fun requestPermissions(context: Context, permission: String) {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(permission),
            permission.hashCode() // Use unique request code based on permission string
        )
    }

    fun onRequestPermissionsResult(requestCode: Int, grantResults: IntArray, onGranted: () -> Unit) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onGranted()
        }
    }
}