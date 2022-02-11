package com.jenzz.peoplenotes.feature.people.ui.dialogs

import android.os.Parcelable
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.jenzz.peoplenotes.R
import com.jenzz.peoplenotes.common.data.people.PersonId
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle
import kotlinx.parcelize.Parcelize

@Destination(style = DestinationStyle.Dialog::class)
@Composable
fun DeletePersonDialog(
    resultBackNavigator: ResultBackNavigator<DeletePersonDialogResult>,
    personId: PersonId,
) {
    AlertDialog(
        text = {
            Text(text = stringResource(id = R.string.delete_person_dialog))
        },
        confirmButton = {
            TextButton(
                onClick = {
                    resultBackNavigator.navigateBack(DeletePersonDialogResult.Yes(personId))
                }
            ) {
                Text(text = stringResource(id = R.string.yes))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    resultBackNavigator.navigateBack(DeletePersonDialogResult.No)
                }
            ) {
                Text(text = stringResource(id = R.string.cancel))
            }
        },
        onDismissRequest = {
            resultBackNavigator.navigateBack(DeletePersonDialogResult.No)
        },
    )
}

sealed class DeletePersonDialogResult : Parcelable {

    @Parcelize
    data class Yes(val personId: PersonId) : DeletePersonDialogResult()

    @Parcelize
    object No : DeletePersonDialogResult()
}
