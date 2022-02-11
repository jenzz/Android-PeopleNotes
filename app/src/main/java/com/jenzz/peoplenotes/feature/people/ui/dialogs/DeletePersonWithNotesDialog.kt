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
fun DeletePersonWithNotesDialog(
    resultBackNavigator: ResultBackNavigator<DeletePersonWithNotesDialogResult>,
    personId: PersonId,
) {
    AlertDialog(
        text = {
            Text(text = stringResource(id = R.string.delete_person_with_notes_dialog))
        },
        confirmButton = {
            TextButton(
                onClick = {
                    resultBackNavigator.navigateBack(
                        DeletePersonWithNotesDialogResult.Yes(personId)
                    )
                }
            ) {
                Text(text = stringResource(id = R.string.yes))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    resultBackNavigator.navigateBack(DeletePersonWithNotesDialogResult.No)
                }
            ) {
                Text(text = stringResource(id = R.string.cancel))
            }
        },
        onDismissRequest = {
            resultBackNavigator.navigateBack(DeletePersonWithNotesDialogResult.No)
        },
    )
}

sealed class DeletePersonWithNotesDialogResult : Parcelable {

    @Parcelize
    data class Yes(val personId: PersonId) : DeletePersonWithNotesDialogResult()

    @Parcelize
    object No : DeletePersonWithNotesDialogResult()
}
