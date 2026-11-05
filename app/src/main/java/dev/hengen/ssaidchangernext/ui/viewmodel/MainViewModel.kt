package dev.hengen.ssaidchangernext.ui.viewmodel

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.topjohnwu.superuser.Shell
import dev.hengen.ssaidchangernext.R
import dev.hengen.ssaidchangernext.data.model.AppItem
import dev.hengen.ssaidchangernext.data.model.SsaidResult
import dev.hengen.ssaidchangernext.data.preferences.AppPreferences
import dev.hengen.ssaidchangernext.engine.SsaidEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class FilterType {
    USER, SYSTEM, ALL
}

data class UiState(
    val hasRoot: Boolean = false,
    val isCheckingRoot: Boolean = true,
    val isLoadingApps: Boolean = false,
    val isRefreshingApps: Boolean = false,
    val allApps: List<AppItem> = emptyList(),
    val filteredApps: List<AppItem> = emptyList(),
    val selectedApp: AppItem? = null,
    val currentSsaid: String? = null,
    val originalSsaid: String? = null,
    val isLoadingSsaid: Boolean = false,
    val isUpdatingSsaid: Boolean = false,
    val searchQuery: String = "",
    val activeFilter: FilterType = FilterType.USER,
    val statusMessage: String? = null,
    val statusIsError: Boolean = false,
    val showRestartDialog: Boolean = false,
    val showBackupSheet: Boolean = false,
    val showLanguageDialog: Boolean = false,
    val selectedLanguage: String = "auto",
    val hasFullBackupFile: Boolean = false,
    val modifiedAppsCount: Int = 0,
    val appVersion: String = "1.5.0",
    val appBuild: Int = 150
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = AppPreferences(application)
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        _uiState.value = _uiState.value.copy(
            selectedLanguage = prefs.getLanguagePreference()
        )
        checkRootAndLoad(forceFresh = false)
    }

    fun requestRoot() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCheckingRoot = true)
            withContext(Dispatchers.IO) {
                try {
                    Shell.getCachedShell()?.close()
                } catch (e: Exception) {
                    // ignored
                }
                try {
                    Shell.cmd("id").exec()
                } catch (e: Exception) {
                    // ignored
                }
            }
            checkRootAndLoad(forceFresh = true)
        }
    }

    fun checkRootAndLoad(forceFresh: Boolean = true) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCheckingRoot = true)
            val root = SsaidEngine.isRootAvailable(forceFresh = forceFresh)
            val hasBak = if (root) SsaidEngine.hasFullBackup() else false
            val modifiedCount = prefs.getAllModifiedPackages().size

            val context = getApplication<Application>()
            _uiState.value = _uiState.value.copy(
                hasRoot = root,
                isCheckingRoot = false,
                hasFullBackupFile = hasBak,
                modifiedAppsCount = modifiedCount,
                // Only show statusMessage if root is denied; clean header handles "Root OK"
                statusMessage = if (!root) context.getString(R.string.root_denied) else null
            )
            if (root) {
                loadInstalledApps()
            }
        }
    }

    /**
     * Ultra-fast app loading (~15ms).
     */
    fun loadInstalledApps() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingApps = true)
            fetchAppsInternal()
            _uiState.value = _uiState.value.copy(isLoadingApps = false)
            applyFilterAndSearch()
        }
    }

    /**
     * Pull-to-refresh action for the app picker list.
     */
    fun refreshApps() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshingApps = true)
            kotlinx.coroutines.delay(450)
            fetchAppsInternal()
            _uiState.value = _uiState.value.copy(isRefreshingApps = false)
            applyFilterAndSearch()
        }
    }

    private suspend fun fetchAppsInternal() = withContext(Dispatchers.IO) {
        val pm = getApplication<Application>().packageManager
        val packages = pm.getInstalledApplications(PackageManager.MATCH_ALL)
        val modifiedSet = prefs.getAllModifiedPackages().keys

        val apps = packages.map { appInfo ->
            val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
            val isMod = modifiedSet.contains(appInfo.packageName)
            AppItem(
                packageName = appInfo.packageName,
                appName = appInfo.loadLabel(pm).toString(),
                uid = appInfo.uid,
                isSystemApp = isSystem,
                iconBitmap = null,
                isModified = isMod
            )
        }.sortedWith(
            compareByDescending<AppItem> { it.isModified }
                .thenBy { it.appName.lowercase() }
        )

        _uiState.value = _uiState.value.copy(allApps = apps)
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        applyFilterAndSearch()
    }

    fun setFilterType(filter: FilterType) {
        _uiState.value = _uiState.value.copy(activeFilter = filter)
        applyFilterAndSearch()
    }

    private fun applyFilterAndSearch() {
        val state = _uiState.value
        val query = state.searchQuery.trim().lowercase()

        val filtered = state.allApps.filter { app ->
            val matchesFilter = when (state.activeFilter) {
                FilterType.USER -> !app.isSystemApp
                FilterType.SYSTEM -> app.isSystemApp
                FilterType.ALL -> true
            }

            val matchesQuery = query.isEmpty() ||
                    app.appName.lowercase().contains(query) ||
                    app.packageName.lowercase().contains(query)

            matchesFilter && matchesQuery
        }

        _uiState.value = state.copy(filteredApps = filtered)
    }

    fun selectApp(app: AppItem) {
        val savedOriginal = prefs.getOriginalSsaid(app.packageName)
        _uiState.value = _uiState.value.copy(
            selectedApp = app,
            currentSsaid = null,
            originalSsaid = savedOriginal
        )
        fetchSsaidForSelected()
    }

    fun clearSelectedApp() {
        _uiState.value = _uiState.value.copy(
            selectedApp = null,
            currentSsaid = null,
            originalSsaid = null,
            statusMessage = null
        )
    }

    fun fetchSsaidForSelected() {
        val app = _uiState.value.selectedApp ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingSsaid = true)
            val ssaid = SsaidEngine.readSsaid(app.packageName)
            val savedOriginal = prefs.getOriginalSsaid(app.packageName)

            val context = getApplication<Application>()
            _uiState.value = _uiState.value.copy(
                currentSsaid = ssaid,
                originalSsaid = savedOriginal,
                isLoadingSsaid = false,
                statusMessage = if (ssaid == null) {
                    context.getString(R.string.no_existing_ssaid)
                } else {
                    context.getString(R.string.loaded_ssaid_format, app.appName)
                }
            )
        }
    }

    fun updateSsaid(newId: String, clearData: Boolean = false) {
        val app = _uiState.value.selectedApp ?: return
        val current = _uiState.value.currentSsaid
        val context = getApplication<Application>()

        viewModelScope.launch {
            if (!current.isNullOrBlank() && prefs.getOriginalSsaid(app.packageName) == null) {
                prefs.saveOriginalSsaid(app.packageName, current)
            }

            _uiState.value = _uiState.value.copy(isUpdatingSsaid = true)

            val result = SsaidEngine.updateSsaid(
                packageName = app.packageName,
                newSsaid = newId,
                stopApp = true
            )

            when (result) {
                is SsaidResult.Success -> {
                    if (clearData) {
                        SsaidEngine.clearAppData(app.packageName)
                    }
                    val updatedOriginal = prefs.getOriginalSsaid(app.packageName)
                    val modifiedCount = prefs.getAllModifiedPackages().size

                    var msg = context.getString(R.string.ssaid_changed_format, app.appName, result.ssaid)
                    if (clearData) {
                        msg += context.getString(R.string.data_cleared_suffix)
                    }

                    _uiState.value = _uiState.value.copy(
                        isUpdatingSsaid = false,
                        currentSsaid = result.ssaid,
                        originalSsaid = updatedOriginal,
                        modifiedAppsCount = modifiedCount,
                        statusMessage = msg,
                        statusIsError = false,
                        showRestartDialog = true
                    )
                    loadInstalledApps()
                }
                is SsaidResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isUpdatingSsaid = false,
                        statusMessage = result.error,
                        statusIsError = true
                    )
                }
            }
        }
    }

    fun restoreSelectedAppToOriginal(clearData: Boolean = true) {
        val app = _uiState.value.selectedApp ?: return
        val orig = _uiState.value.originalSsaid ?: return
        val context = getApplication<Application>()

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUpdatingSsaid = true)

            val result = SsaidEngine.updateSsaid(
                packageName = app.packageName,
                newSsaid = orig,
                stopApp = true
            )

            when (result) {
                is SsaidResult.Success -> {
                    if (clearData) {
                        SsaidEngine.clearAppData(app.packageName)
                    }
                    prefs.removeOriginalSsaid(app.packageName)
                    val modifiedCount = prefs.getAllModifiedPackages().size

                    _uiState.value = _uiState.value.copy(
                        isUpdatingSsaid = false,
                        currentSsaid = orig,
                        originalSsaid = null,
                        modifiedAppsCount = modifiedCount,
                        statusMessage = context.getString(R.string.restored_to_original_format, app.appName, orig),
                        statusIsError = false,
                        showRestartDialog = true
                    )
                    loadInstalledApps()
                }
                is SsaidResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isUpdatingSsaid = false,
                        statusMessage = "Restore failed: ${result.error}",
                        statusIsError = true
                    )
                }
            }
        }
    }

    fun restoreFullDatabase() {
        val context = getApplication<Application>()
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUpdatingSsaid = true)
            val result = SsaidEngine.restoreFullBackup()

            when (result) {
                is SsaidResult.Success -> {
                    prefs.clearAll()
                    _uiState.value = _uiState.value.copy(
                        isUpdatingSsaid = false,
                        originalSsaid = null,
                        modifiedAppsCount = 0,
                        showBackupSheet = false,
                        statusMessage = context.getString(R.string.all_restored_success),
                        statusIsError = false,
                        showRestartDialog = true
                    )
                    loadInstalledApps()
                    fetchSsaidForSelected()
                }
                is SsaidResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isUpdatingSsaid = false,
                        statusMessage = result.error,
                        statusIsError = true
                    )
                }
            }
        }
    }

    fun setLanguage(languageCode: String, onDone: () -> Unit) {
        prefs.setLanguagePreference(languageCode)
        _uiState.value = _uiState.value.copy(
            selectedLanguage = languageCode,
            showLanguageDialog = false
        )
        onDone()
    }

    fun setLanguageDialogVisible(visible: Boolean) {
        _uiState.value = _uiState.value.copy(showLanguageDialog = visible)
    }

    fun setBackupSheetVisible(visible: Boolean) {
        viewModelScope.launch {
            val hasBak = SsaidEngine.hasFullBackup()
            val modifiedCount = prefs.getAllModifiedPackages().size
            _uiState.value = _uiState.value.copy(
                showBackupSheet = visible,
                hasFullBackupFile = hasBak,
                modifiedAppsCount = modifiedCount
            )
        }
    }

    fun dismissRestartDialog() {
        _uiState.value = _uiState.value.copy(showRestartDialog = false)
    }

    fun rebootDevice() {
        viewModelScope.launch {
            SsaidEngine.rebootDevice()
        }
    }

    fun getRandomSsaid(): String {
        return SsaidEngine.generateRandomSsaid()
    }
}
