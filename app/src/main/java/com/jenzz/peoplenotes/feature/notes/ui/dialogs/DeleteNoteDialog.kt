package com.jenzz.peoplenotes.feature.notes.ui.dialogs

import android.os.Parcelable
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.jenzz.peoplenotes.R
import com.jenzz.peoplenotes.common.data.notes.NoteId
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle
import kotlinx.parcelize.Parcelize

@Destination(style = DestinationStyle.Dialog::class)
@Composable
fun DeleteNoteDialog(
    resultBackNavigator: ResultBackNavigator<DeleteNoteDialogResult>,
    noteId: NoteId,
) {
    AlertDialog(
        text = {
            Text(text = stringResource(id = R.string.delete_note_dialog))
        },
        confirmButton = {
            TextButton(
                onClick = {
                    resultBackNavigator.navigateBack(DeleteNoteDialogResult.Yes(noteId))
                }
            ) {
                Text(text = stringResource(id = R.string.yes))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    resultBackNavigator.navigateBack(DeleteNoteDialogResult.No)
                }
            ) {
                Text(text = stringResource(id = R.string.cancel))
            }
        },
        onDismissRequest = {
            resultBackNavigator.navigateBack(DeleteNoteDialogResult.No)
        },
    )
}

sealed class DeleteNoteDialogResult : Parcelable {

    @Parcelize
    data class Yes(val personId: NoteId) : DeleteNoteDialogResult()

    @Parcelize
    object No : DeleteNoteDialogResult()
}
