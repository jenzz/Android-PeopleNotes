package com.jenzz.peoplenotes.feature.settings.ui

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.CollectionPreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jenzz.peoplenotes.BuildConfig
import com.jenzz.peoplenotes.R
import com.jenzz.peoplenotes.common.ui.theme.PeopleNotesTheme
import com.jenzz.peoplenotes.common.ui.theme.spacing
import com.jenzz.peoplenotes.common.ui.widgets.LoadingView
import com.jenzz.peoplenotes.ext.rememberFlowWithLifecycle
import com.jenzz.peoplenotes.feature.settings.data.Settings
import com.jenzz.peoplenotes.feature.settings.data.ThemePreference
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination(start = true, navGraph = "settings")
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    navigator: DestinationsNavigator,
) {
    val state by rememberFlowWithLifecycle(viewModel.state)
        .collectAsState(initial = viewModel.initialState)
    SettingsContent(
        state = state,
        onNavigateUp = navigator::navigateUp,
        onThemeChange = viewModel::onThemeChange,
    )
}

@Composable
private fun SettingsContent(
    state: SettingsUiState,
    onNavigateUp: () -> Unit,
    onThemeChange: (ThemePreference) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                },
                title = {
                    Text(text = stringResource(id = R.string.settings))
                }
            )
        }
    ) {
        when (state) {
            is SettingsUiState.InitialLoad ->
                LoadingView()
            is SettingsUiState.Loaded ->
                SettingsLoaded(
                    settings = state.settings,
                    onThemeChange = onThemeChange,
                )
        }
    }
}

@Composable
private fun SettingsLoaded(
    settings: Settings,
    onThemeChange: (ThemePreference) -> Unit,
) {
    Column {
        SettingsThemeItem(
            selectedTheme = settings.theme,
            onThemeChange = onThemeChange,
        )
        SettingsDivider()
        SettingsAppVersionItem()
    }
}

@Composable
private fun SettingsThemeItem(
    selectedTheme: ThemePreference,
    onThemeChange: (ThemePreference) -> Unit,
) {
    var showDropDown by rememberSaveable { mutableStateOf(false) }
    Box {
        SettingsItem(
            label = R.string.theme,
            value = stringResource(id = selectedTheme.label),
            enabled = true,
            onClick = { showDropDown = true },
        )
        SettingsThemeDropDownMenu(
            visible = showDropDown,
            onDismissRequest = { showDropDown = false },
            selectedTheme = selectedTheme,
            onThemeChange = { theme ->
                showDropDown = false
                onThemeChange(theme)
            }
        )
    }
}

@Composable
fun SettingsItem(
    @StringRes label: Int,
    value: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = MaterialTheme.spacing.large),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(id = label),
            fontWeight = FontWeight.Bold,
        )
        Text(text = value)
    }
}

@Composable
private fun SettingsThemeDropDownMenu(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    selectedTheme: ThemePreference,
    onThemeChange: (ThemePreference) -> Unit,
) {
    DropdownMenu(
        expanded = visible,
        onDismissRequest = onDismissRequest,
    ) {
        ThemePreference.values().forEach { theme ->
            SettingsThemeDropDownItem(
                theme = theme,
                isSelected = theme == selectedTheme,
                onClick = { onThemeChange(theme) },
            )
        }
    }
}

@Composable
fun SettingsAppVersionItem() {
    SettingsItem(
        label = R.string.app_version,
        value = BuildConfig.VERSION_NAME,
        enabled = false,
        onClick = {}
    )
}

@Composable
fun SettingsDivider() {
    Divider(thickness = 48.dp)
}

@Composable
private fun SettingsThemeDropDownItem(
    theme: ThemePreference,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    DropdownMenuItem(onClick = onClick) {
        RadioButton(
            selected = isSelected,
            onClick = onClick,
        )
        Text(
            modifier = Modifier.padding(start = MaterialTheme.spacing.medium),
            text = stringResource(id = theme.label),
        )
    }
}

@Preview(
    name = "Light Mode",
    uiMode = UI_MODE_NIGHT_NO,
)
@Preview(
    name = "Dark Mode",
    uiMode = UI_MODE_NIGHT_YES,
)
@Composable
private fun SettingsContentPreview(
    @PreviewParameter(SettingsPreviewParameterProvider::class)
    state: SettingsUiState,
) {
    PeopleNotesTheme {
        Surface {
            SettingsContent(
                state = state,
                onNavigateUp = {},
                onThemeChange = {},
            )
        }
    }
}

class SettingsPreviewParameterProvider : CollectionPreviewParameterProvider<SettingsUiState>(
    listOf(
        SettingsUiState.InitialLoad,
        SettingsUiState.Loaded(
            settings = Settings(
                theme = ThemePreference.DEFAULT,
            )
        ),
    )
)
