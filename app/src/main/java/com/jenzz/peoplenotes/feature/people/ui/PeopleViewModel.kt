package com.jenzz.peoplenotes.feature.people.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.jakewharton.rxrelay3.BehaviorRelay
import com.jenzz.peoplenotes.common.data.people.DeletePersonResult
import com.jenzz.peoplenotes.common.data.people.PersonId
import com.jenzz.peoplenotes.common.ui.ToastMessageId
import com.jenzz.peoplenotes.common.ui.widgets.SearchBarState
import com.jenzz.peoplenotes.feature.people.data.PeopleUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.util.*
import javax.inject.Inject

@HiltViewModel
class PeopleViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val useCases: PeopleUseCases,
) : ViewModel() {

    val initialState = PeopleUiState()

    //    private val toastMessageManager = ToastMessageManager()
    private val isLoading = BehaviorRelay.createDefault(initialState.isLoading)
    private val searchBarState = BehaviorRelay.createDefault(initialState.searchBarState)
    private val people = BehaviorRelay.createDefault(initialState.people)
    private val showDeleteConfirmation =
        BehaviorRelay.createDefault(initialState.showDeleteConfirmation)
    private val showDeleteWithNotesConfirmation =
        BehaviorRelay.createDefault(initialState.showDeleteWithNotesConfirmation)

    private val disposables = CompositeDisposable()

    val state = Observable.combineLatest(
        searchBarState,
        isLoading,
        people,
        showDeleteConfirmation,
        showDeleteWithNotesConfirmation,
//        toastMessageManager.message,
        ::PeopleUiState,
    )

    init {
        disposables.add(
            searchBarState
                .flatMap { state ->
                    useCases.observePeople(
                        sortBy = state.sortBy.selected,
                        filter = state.searchTerm,
                    )
                }
                .forEach { people ->
                    this.isLoading.accept(false)
                    this.people.accept(people)
                }
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    fun onSearchBarStateChange(state: SearchBarState) {
        searchBarState.accept(state)
    }

    fun onDelete(personId: PersonId) {
        showDeleteConfirmation.accept(Optional.of(personId))
    }

    fun onDeleteCancel() {
        showDeleteConfirmation.accept(Optional.empty())
    }

    fun onDeleteConfirm(personId: PersonId) {
        isLoading.accept(true)
        showDeleteConfirmation.accept(Optional.empty())
        disposables.add(
            useCases
                .deletePerson(personId)
                .subscribe { result ->
                    when (result) {
                        is DeletePersonResult.RemainingNotesForPerson -> {
                            isLoading.accept(false)
                            showDeleteWithNotesConfirmation.accept(Optional.of(personId))
                        }
                        is DeletePersonResult.Success -> {
                            isLoading.accept(false)
//                            toastMessageManager.emitMessage(
//                                ToastMessage(text = TextResource.fromId(R.string.person_deleted))
//                            )
                        }
                    }
                }
        )
    }

    fun onDeleteWithNotesConfirm(personId: PersonId) {
        isLoading.accept(true)
        showDeleteWithNotesConfirmation.accept(Optional.empty())
        disposables.add(
            useCases.deletePersonWithNotes(personId).subscribe()
        )
        isLoading.accept(false)
//        toastMessageManager.emitMessage(
//            ToastMessage(text = TextResource.fromId(id = R.string.person_deleted))
//        )
    }

    fun onDeleteWithNotesCancel() {
        showDeleteWithNotesConfirmation.accept(Optional.empty())
    }

    fun onToastShown(id: ToastMessageId) {
//        viewModelScope.launch {
//            toastMessageManager.clearMessage(id)
//        }
    }
}
