package com.jenzz.peoplenotes.common.data.people

import com.jenzz.peoplenotes.common.data.notes.NewNote
import com.jenzz.peoplenotes.common.data.notes.NotesList
import com.jenzz.peoplenotes.common.data.notes.NotesRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class PeopleAndNotesRepository @Inject constructor(
    private val peopleRepository: PeopleRepository,
    private val notesRepository: NotesRepository,
) {

    suspend fun add(newPerson: NewPerson, newNote: NewNote?) {
        val person = peopleRepository.add(newPerson)
        if (newNote != null) {
            notesRepository.add(newNote, person.id)
        }
    }

    fun delete(personId: PersonId): Single<DeletePersonResult> =
        notesRepository
            .observeNotes(personId)
            .first(NotesList())
            .flatMap { notes ->
                if (notes.isEmpty) {
                    peopleRepository
                        .delete(personId)
                        .toSingle<DeletePersonResult> { DeletePersonResult.Success }
                } else {
                    Single.just<DeletePersonResult>(
                        DeletePersonResult.RemainingNotesForPerson(personId, notes)
                    )
                }
            }

    fun deleteWithNotes(personId: PersonId): Completable =
        notesRepository.deleteAllByPerson(personId)
            .andThen(peopleRepository.delete(personId))
}

sealed class DeletePersonResult {

    object Success : DeletePersonResult()

    data class RemainingNotesForPerson(
        val personId: PersonId,
        val notes: NotesList,
    ) : DeletePersonResult()
}
