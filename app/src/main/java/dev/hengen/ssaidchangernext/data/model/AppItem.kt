package dev.hengen.ssaidchangernext.data.model

data class AppItem(
    val packageName: String,
    val appName: String,
    val isSystemApp: Boolean,
    val isModified: Boolean = false
)

sealed class SsaidResult {
    data class Success(val ssaid: String, val message: String) : SsaidResult()
    data class Error(val error: String) : SsaidResult()
}
