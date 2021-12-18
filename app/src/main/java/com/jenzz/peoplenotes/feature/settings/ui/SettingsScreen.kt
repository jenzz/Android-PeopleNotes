package com.jenzz.peoplenotes.feature.settings.ui

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.CollectionPreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jenzz.peoplenotes.R
import com.jenzz.peoplenotes.common.ui.theme.PeopleNotesTheme
import com.jenzz.peoplenotes.feature.settings.data.Settings
import com.jenzz.peoplenotes.feature.settings.data.ThemePreference

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state = viewModel.state.value
    SettingsContent(
        state = state,
        onThemeSelected = viewModel::onThemeSelected,
    )
}

@Composable
private fun SettingsContent(
    state: SettingsUiState,
    onThemeSelected: (ThemePreference) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.settings))
                }
            )
        }
    ) {
        when (state) {
            is SettingsUiState.Loading ->
                SettingsLoading()
            is SettingsUiState.Loaded ->
                SettingsLoaded(
                    settings = state.settings,
                    onThemeSelected = onThemeSelected,
                )
        }
    }
}

@Composable
private fun SettingsLoading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun SettingsLoaded(
    settings: Settings,
    onThemeSelected: (ThemePreference) -> Unit,
) {
    Column {
        SettingsThemeItem(
            modifier = Modifier.fillMaxWidth(),
            selectedTheme = settings.theme,
            onThemeSelected = onThemeSelected,
        )
    }
}

@Composable
private fun SettingsThemeItem(
    modifier: Modifier = Modifier,
    selectedTheme: ThemePreference,
    onThemeSelected: (ThemePreference) -> Unit,
) {
    var showDropDown by rememberSaveable { mutableStateOf(false) }
    Column {
        Column(
            modifier = modifier
                .clickable(
                    onClick = { showDropDown = true }
                )
                .padding(2.dp),
        ) {
            Text(
                text = stringResource(id = R.string.theme),
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(id = selectedTheme.label),
            )
        }
        Divider()
        SettingsThemeDropDownMenu(
            visible = showDropDown,
            onDismissRequest = { showDropDown = false },
            selectedTheme = selectedTheme,
            onThemeSelected = { theme ->
                showDropDown = false
                onThemeSelected(theme)
            }
        )
    }
}

@Composable
private fun SettingsThemeDropDownMenu(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    selectedTheme: ThemePreference,
    onThemeSelected: (ThemePreference) -> Unit,
) {
    DropdownMenu(
        expanded = visible,
        onDismissRequest = onDismissRequest,
    ) {
        ThemePreference.values().forEach { theme ->
            SettingsThemeDropDownItem(
                theme = theme,
                isSelected = theme == selectedTheme,
                onClick = { onThemeSelected(theme) },
            )
        }
    }
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
            modifier = Modifier.padding(start = 8.dp),
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
                onThemeSelected = {},
            )
        }
    }
}

class SettingsPreviewParameterProvider : CollectionPreviewParameterProvider<SettingsUiState>(
    listOf(
        SettingsUiState.Loading,
        SettingsUiState.Loaded(
            settings = Settings(
                theme = ThemePreference.DEFAULT,
            )
        ),
    )
)
