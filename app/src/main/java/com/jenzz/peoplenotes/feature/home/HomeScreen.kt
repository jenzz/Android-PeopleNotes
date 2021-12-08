package com.jenzz.peoplenotes.feature.home

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jenzz.peoplenotes.ui.theme.PeopleNotesTheme

@Composable
fun HomeScreen(
    onSettingsClicked: () -> Unit,
) {
    Column {
        Text(text = "Hello Home!")
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = onSettingsClicked) {
            Text(text = "Go To Settings")
        }
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
fun HomeScreenPreview() {
    PeopleNotesTheme {
        Surface {
            HomeScreen(
                onSettingsClicked = {},
            )
        }
    }
}
