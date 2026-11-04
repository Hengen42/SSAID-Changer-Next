package dev.hengen.ssaidchangernext.ui.screen

import android.graphics.Bitmap
import android.util.LruCache
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.graphics.drawable.toBitmap
import dev.hengen.ssaidchangernext.R
import dev.hengen.ssaidchangernext.data.model.AppItem
import dev.hengen.ssaidchangernext.ui.theme.*
import dev.hengen.ssaidchangernext.ui.viewmodel.FilterType
import dev.hengen.ssaidchangernext.ui.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val iconCache = LruCache<String, Bitmap>(250)

@Composable
fun AsyncAppIcon(
    packageName: String,
    modifier: Modifier = Modifier,
    fallbackText: String = "?"
) {
    val context = LocalContext.current
    var bitmap by remember(packageName) { mutableStateOf(iconCache.get(packageName)) }

    LaunchedEffect(packageName) {
        if (bitmap == null) {
            withContext(Dispatchers.IO) {
                try {
                    val pm = context.packageManager
                    val appInfo = pm.getApplicationInfo(packageName, 0)
                    val drawable = pm.getApplicationIcon(appInfo)
                    val bmp = drawable.toBitmap(width = 96, height = 96)
                    iconCache.put(packageName, bmp)
                    bitmap = bmp
                } catch (e: Exception) {
                    // Ignore, fallback will show
                }
            }
        }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap!!.asImageBitmap(),
            contentDescription = null,
            modifier = modifier
        )
    } else {
        Box(
            modifier = modifier.background(NextBackground),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = fallbackText.firstOrNull()?.uppercase() ?: "?",
                color = NextAccent,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onLanguageChanged: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var newIdInput by remember { mutableStateOf("") }
    var alsoClearData by remember { mutableStateOf(true) }
    var showAppPicker by remember { mutableStateOf(false) }
    var showFullRestoreConfirmDialog by remember { mutableStateOf(false) }

    LaunchedEffect(state.currentSsaid) {
        if (state.currentSsaid != null) {
            newIdInput = state.currentSsaid!!
        }
    }

    // BackHandler 1: If full-screen App Picker is open, back closes it
    BackHandler(enabled = showAppPicker) {
        showAppPicker = false
    }

    // BackHandler 2: If an app is selected on main screen, back clears selection first
    BackHandler(enabled = !showAppPicker && state.selectedApp != null) {
        viewModel.clearSelectedApp()
    }

    // If full-screen App Picker is requested, show full screen view
    if (showAppPicker) {
        FullScreenAppPicker(
            viewModel = viewModel,
            onBack = { showAppPicker = false },
            onAppSelected = { app ->
                viewModel.selectApp(app)
                showAppPicker = false
            }
        )
        return
    }

    // Language Selection Dialog
    if (state.showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.setLanguageDialogVisible(false) },
            containerColor = NextSurface,
            shape = RoundedCornerShape(14.dp),
            title = {
                Text(
                    text = stringResource(R.string.change_language),
                    style = MaterialTheme.typography.titleMedium,
                    color = NextTextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    LanguageOptionRow(
                        title = stringResource(R.string.lang_auto),
                        isSelected = state.selectedLanguage == "auto",
                        onClick = {
                            viewModel.setLanguage("auto", onLanguageChanged)
                        }
                    )
                    LanguageOptionRow(
                        title = stringResource(R.string.lang_tr),
                        isSelected = state.selectedLanguage == "tr",
                        onClick = {
                            viewModel.setLanguage("tr", onLanguageChanged)
                        }
                    )
                    LanguageOptionRow(
                        title = stringResource(R.string.lang_en),
                        isSelected = state.selectedLanguage == "en",
                        onClick = {
                            viewModel.setLanguage("en", onLanguageChanged)
                        }
                    )
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(
                    onClick = { viewModel.setLanguageDialogVisible(false) },
                    colors = ButtonDefaults.textButtonColors(contentColor = NextTextSecondary)
                ) {
                    Text(stringResource(R.string.cancel_btn))
                }
            }
        )
    }

    // Root Gate: If Root is NOT available, block main screen completely
    if (!state.hasRoot) {
        RootRequiredScreen(
            isChecking = state.isCheckingRoot,
            onRequestRoot = { viewModel.requestRoot() },
            onCheckAgain = { viewModel.checkRootAndLoad(forceFresh = true) },
            onLanguageClick = { viewModel.setLanguageDialogVisible(true) }
        )
        return
    }

    // Critical Restart Required Dialog (With Framed Dynamic Warning Button)
    if (state.showRestartDialog) {
        val targetPkg = state.selectedApp?.packageName ?: ""
        AlertDialog(
            onDismissRequest = { viewModel.dismissRestartDialog() },
            containerColor = NextSurface,
            shape = RoundedCornerShape(14.dp),
            icon = {
                Icon(
                    imageVector = Icons.Default.WarningAmber,
                    contentDescription = null,
                    tint = NextError,
                    modifier = Modifier.size(40.dp)
                )
            },
            title = {
                Text(
                    text = stringResource(R.string.restart_dialog_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = NextError,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = NextError.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NextError.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.restart_dialog_warning_bold),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = NextError
                            ),
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = stringResource(R.string.restart_dialog_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = NextTextSecondary
                    )
                }
            },
            confirmButton = {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            viewModel.dismissRestartDialog()
                            viewModel.rebootDevice()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NextAccent,
                            contentColor = NextBackground
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(stringResource(R.string.restart_now_btn), fontWeight = FontWeight.Bold)
                    }

                    // Framed Warning Dismiss Button
                    OutlinedButton(
                        onClick = { viewModel.dismissRestartDialog() },
                        border = androidx.compose.foundation.BorderStroke(1.dp, NextError.copy(alpha = 0.6f)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = NextError.copy(alpha = 0.08f),
                            contentColor = NextError
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.later_btn_format, targetPkg),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            },
            dismissButton = {}
        )
    }

    // Full Restore Confirm Dialog
    if (showFullRestoreConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showFullRestoreConfirmDialog = false },
            containerColor = NextSurface,
            shape = RoundedCornerShape(14.dp),
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = NextWarning,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = stringResource(R.string.restore_confirm_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = NextTextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.restore_confirm_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = NextTextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showFullRestoreConfirmDialog = false
                        viewModel.restoreFullDatabase()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NextError,
                        contentColor = NextTextPrimary
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(stringResource(R.string.restore_confirm_yes), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showFullRestoreConfirmDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = NextTextSecondary)
                ) {
                    Text(stringResource(R.string.cancel_btn))
                }
            }
        )
    }

    Scaffold(
        containerColor = NextBackground,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NextBackground
                ),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_app_logo),
                            contentDescription = "Logo",
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(verticalArrangement = Arrangement.Center) {
                            Text(
                                text = stringResource(R.string.app_name),
                                color = NextTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                fontFamily = FontFamily.Monospace,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(if (state.hasRoot) NextSuccess else NextError)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = if (state.hasRoot) stringResource(R.string.root_status_ok) else stringResource(R.string.root_status_no),
                                    color = if (state.hasRoot) NextSuccess else NextError,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "•",
                                    color = NextTextSecondary,
                                    fontSize = 10.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = stringResource(R.string.version_format, state.appVersion),
                                    color = NextAccent,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                },
                actions = {
                    // Language Picker Button
                    IconButton(onClick = { viewModel.setLanguageDialogVisible(true) }) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Language",
                            tint = NextAccent
                        )
                    }

                    // Backup & Restore Sheet Button
                    IconButton(onClick = { viewModel.setBackupSheetVisible(true) }) {
                        Icon(
                            imageVector = Icons.Default.SettingsBackupRestore,
                            contentDescription = "Backup & Restore",
                            tint = NextTextSecondary
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Status Banner
            state.statusMessage?.let { msg ->
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (state.statusIsError) NextError.copy(alpha = 0.15f) else NextSurfaceVariant
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Text(
                        text = msg,
                        color = if (state.statusIsError) NextError else NextTextSecondary,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            // Target App Selection Card
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = NextSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showAppPicker = true }
                    .border(1.dp, NextBorder, RoundedCornerShape(12.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val app = state.selectedApp
                    if (app != null) {
                        AsyncAppIcon(
                            packageName = app.packageName,
                            fallbackText = app.appName,
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Apps,
                            contentDescription = null,
                            tint = NextAccent,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = app?.appName ?: stringResource(R.string.select_target_app),
                            color = NextTextPrimary,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = app?.packageName ?: stringResource(R.string.touch_to_pick),
                            color = NextTextSecondary,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = NextTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Current SSAID Details Card
            if (state.selectedApp != null) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = NextSurface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, NextBorder, RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.current_active_ssaid),
                                style = MaterialTheme.typography.labelSmall,
                                color = NextTextSecondary
                            )
                            if (state.isLoadingSsaid) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    strokeWidth = 2.dp,
                                    color = NextAccent
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        val displayId = state.currentSsaid ?: stringResource(R.string.not_found_in_database)
                        Text(
                            text = displayId,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            color = if (state.currentSsaid != null) NextAccent else NextWarning,
                            modifier = Modifier.clickable {
                                if (state.currentSsaid != null) {
                                    clipboardManager.setText(AnnotatedString(state.currentSsaid!!))
                                    Toast.makeText(context, context.getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show()
                                }
                            }
                        )

                        // Per-App Restore Original Card
                        if (state.originalSsaid != null && state.originalSsaid != state.currentSsaid) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = NextWarning.copy(alpha = 0.12f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, NextWarning.copy(alpha = 0.35f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = stringResource(R.string.factory_original_id),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = NextWarning
                                        )
                                        Text(
                                            text = state.originalSsaid!!,
                                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                                            color = NextTextPrimary
                                        )
                                    }
                                    Button(
                                        onClick = { viewModel.restoreSelectedAppToOriginal(clearData = alsoClearData) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = NextWarning,
                                            contentColor = NextBackground
                                        ),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(stringResource(R.string.restore_btn), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // New SSAID Input
                        OutlinedTextField(
                            value = newIdInput,
                            onValueChange = { input ->
                                if (input.length <= 16 && input.all { it in "0123456789abcdefABCDEF" }) {
                                    newIdInput = input.lowercase()
                                }
                            },
                            label = { Text(stringResource(R.string.new_ssaid_label)) },
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NextAccent,
                                unfocusedBorderColor = NextBorder,
                                focusedLabelColor = NextAccent,
                                cursorColor = NextAccent
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Single Full-Width Randomize Button (Clean!)
                        OutlinedButton(
                            onClick = { newIdInput = viewModel.getRandomSsaid() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = NextAccent),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NextAccent)
                        ) {
                            Icon(Icons.Default.Casino, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.randomize_btn), fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Clear Data Checkbox (Simplified Text)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { alsoClearData = !alsoClearData }
                        ) {
                            Checkbox(
                                checked = alsoClearData,
                                onCheckedChange = { alsoClearData = it },
                                colors = CheckboxDefaults.colors(checkedColor = NextAccent)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(R.string.clear_data_checkbox),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = NextTextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Apply Button
                        Button(
                            onClick = {
                                viewModel.updateSsaid(newIdInput, clearData = alsoClearData)
                            },
                            enabled = !state.isUpdatingSsaid && newIdInput.length == 16,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NextAccent,
                                contentColor = NextBackground
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                        ) {
                            if (state.isUpdatingSsaid) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = NextBackground,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(Icons.Default.SecurityUpdateGood, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(stringResource(R.string.apply_spoofed_ssaid), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_app_logo),
                            contentDescription = null,
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = stringResource(R.string.empty_app_placeholder),
                            color = NextTextSecondary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }

    // Full Backup & Restore Bottom Sheet
    if (state.showBackupSheet) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.setBackupSheetVisible(false) },
            containerColor = NextSurface,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.SettingsBackupRestore,
                        contentDescription = null,
                        tint = NextAccent,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = stringResource(R.string.backup_restore_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = NextTextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = NextSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(stringResource(R.string.full_backup_file), style = MaterialTheme.typography.labelSmall, color = NextTextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (state.hasFullBackupFile) stringResource(R.string.backup_available) else stringResource(R.string.backup_not_found),
                                color = if (state.hasFullBackupFile) NextSuccess else NextWarning,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = NextSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(stringResource(R.string.modified_apps), style = MaterialTheme.typography.labelSmall, color = NextTextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stringResource(R.string.modified_apps_count_format, state.modifiedAppsCount),
                                color = NextAccent,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = stringResource(R.string.backup_explanation),
                    style = MaterialTheme.typography.bodySmall,
                    color = NextTextSecondary
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { showFullRestoreConfirmDialog = true },
                    enabled = state.hasFullBackupFile,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NextError,
                        contentColor = NextTextPrimary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                ) {
                    Icon(Icons.Default.RestorePage, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.restore_entire_backup), fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun LanguageOptionRow(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) NextAccent.copy(alpha = 0.15f) else NextSurfaceVariant,
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, NextAccent) else null,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) NextAccent else NextTextPrimary
            )
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = NextAccent,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun RootRequiredScreen(
    isChecking: Boolean,
    onRequestRoot: () -> Unit,
    onCheckAgain: () -> Unit,
    onLanguageClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NextBackground)
            .padding(24.dp)
    ) {
        IconButton(
            onClick = onLanguageClick,
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(
                imageVector = Icons.Default.Language,
                contentDescription = "Language",
                tint = NextAccent
            )
        }

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_app_logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(16.dp))
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.root_required_title),
                color = NextError,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                fontFamily = FontFamily.Monospace
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.root_required_desc),
                color = NextTextSecondary,
                style = MaterialTheme.typography.bodySmall,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onRequestRoot,
                enabled = !isChecking,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NextAccent,
                    contentColor = NextBackground
                ),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(46.dp)
            ) {
                if (isChecking) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = NextBackground,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Default.Security, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.grant_root), fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onCheckAgain,
                enabled = !isChecking,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = NextTextSecondary),
                border = androidx.compose.foundation.BorderStroke(1.dp, NextBorder),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(40.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(stringResource(R.string.reload_btn))
            }
        }
    }
}

/**
 * FULL SCREEN App Picker Screen (No bottom sheet gesture conflicts, pure Pull-to-Refresh!)
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun FullScreenAppPicker(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onAppSelected: (AppItem) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isRefreshingApps,
        onRefresh = { viewModel.refreshApps() },
        refreshThreshold = 65.dp,
        refreshingOffset = 75.dp
    )

    Scaffold(
        containerColor = NextBackground,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NextBackground),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = NextAccent
                        )
                    }
                },
                title = {
                    Text(
                        text = stringResource(R.string.choose_application),
                        style = MaterialTheme.typography.titleMedium,
                        color = NextTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Search Bar
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text(stringResource(R.string.search_placeholder)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = NextAccent) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NextAccent,
                    unfocusedBorderColor = NextBorder
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Filter Tabs (User / System / All)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterTabButton(stringResource(R.string.tab_user_apps), state.activeFilter == FilterType.USER) {
                    viewModel.setFilterType(FilterType.USER)
                }
                FilterTabButton(stringResource(R.string.tab_system_apps), state.activeFilter == FilterType.SYSTEM) {
                    viewModel.setFilterType(FilterType.SYSTEM)
                }
                FilterTabButton(stringResource(R.string.tab_all_apps), state.activeFilter == FilterType.ALL) {
                    viewModel.setFilterType(FilterType.ALL)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Pure Full-Screen Pull-to-Refresh Container
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pullRefresh(pullRefreshState)
            ) {
                // Highly visible top-center indicator
                PullRefreshIndicator(
                    refreshing = state.isRefreshingApps,
                    state = pullRefreshState,
                    backgroundColor = NextSurfaceVariant,
                    contentColor = NextAccent,
                    scale = true,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .zIndex(10f)
                )

                if (state.isLoadingApps) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = NextAccent)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.filteredApps, key = { it.packageName }) { app ->
                            AppRow(app = app, onClick = { onAppSelected(app) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FilterTabButton(title: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) NextAccent else NextSurfaceVariant,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = title,
            color = if (isSelected) NextBackground else NextTextSecondary,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun AppRow(app: AppItem, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (app.isModified) NextAccent.copy(alpha = 0.08f) else NextSurfaceVariant,
        border = if (app.isModified) androidx.compose.foundation.BorderStroke(1.dp, NextAccent.copy(alpha = 0.4f)) else null,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncAppIcon(
                packageName = app.packageName,
                fallbackText = app.appName,
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(6.dp))
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = app.appName,
                        color = NextTextPrimary,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (app.isModified) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = NextAccent,
                            contentColor = NextBackground
                        ) {
                            Text(
                                text = stringResource(R.string.modified_badge),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Text(
                    text = app.packageName,
                    color = NextTextSecondary,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp
                )
            }
        }
    }
}
