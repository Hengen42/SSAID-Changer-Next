package dev.hengen.ssaidchangernext.data.model

import android.graphics.Bitmap

data class AppItem(
    val packageName: String,
    val appName: String,
    val uid: Int,
    val isSystemApp: Boolean,
    val iconBitmap: Bitmap? = null,
    val currentSsaid: String? = null,
    val isModified: Boolean = false
)

sealed class SsaidResult {
    data class Success(val ssaid: String, val message: String) : SsaidResult()
    data class Error(val error: String) : SsaidResult()
}
