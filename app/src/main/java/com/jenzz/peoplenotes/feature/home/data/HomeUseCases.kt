package com.jenzz.peoplenotes.feature.home.data

import com.jenzz.peoplenotes.common.data.notes.Note
import com.jenzz.peoplenotes.common.data.notes.NoteId
import com.jenzz.peoplenotes.common.data.notes.NotesRepository
import com.jenzz.peoplenotes.feature.home.ui.SortBy
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HomeUseCases @Inject constructor(
    val getNotes: GetNotesUseCase,
    val deleteNote: DeleteNoteUseCase,
)

class GetNotesUseCase @Inject constructor(
    private val notesRepository: NotesRepository,
) {

    operator fun invoke(sortBy: SortBy, filter: String): Flow<List<Note>> =
        notesRepository.getAllNotes(sortBy, filter)
}

class DeleteNoteUseCase @Inject constructor(
    private val notesRepository: NotesRepository,
) {

    suspend operator fun invoke(id: NoteId) {
        notesRepository.delete(id)
        // TODO JD Delete person when zero notes left.
    }
}
