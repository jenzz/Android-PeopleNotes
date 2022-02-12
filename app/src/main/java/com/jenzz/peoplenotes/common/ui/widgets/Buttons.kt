package com.jenzz.peoplenotes.common.ui.widgets

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.jenzz.peoplenotes.common.ui.theme.spacing

@Composable
fun SubmitButton(
    enabled: Boolean,
    @StringRes text: Int,
    onClick: () -> Unit,
) {
    Button(
        modifier = Modifier.padding(top = MaterialTheme.spacing.medium),
        enabled = enabled,
        onClick = onClick,
    ) {
        Text(text = stringResource(id = text))
    }
}
