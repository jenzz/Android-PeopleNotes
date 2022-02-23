package com.jenzz.peoplenotes.feature.people.ui

import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth.assertThat
import com.jakewharton.rxrelay3.BehaviorRelay
import com.jenzz.peoplenotes.common.data.notes.FakeNotesDataSource
import com.jenzz.peoplenotes.common.data.notes.NotesRepository
import com.jenzz.peoplenotes.common.data.people.*
import com.jenzz.peoplenotes.feature.people.data.DeletePersonUseCase
import com.jenzz.peoplenotes.feature.people.data.DeletePersonWithNotesUseCase
import com.jenzz.peoplenotes.feature.people.data.ObservePeopleUseCase
import com.jenzz.peoplenotes.feature.people.data.PeopleUseCases
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import org.junit.Test
import java.util.*

class PeopleViewModelTest {

    private val localPeopleDataSource = FakePeopleDataSource()
    private val localNotesDataSource = FakeNotesDataSource()

    private val sut = PeopleViewModel(
        savedStateHandle = SavedStateHandle(),
        useCases = PeopleUseCases(
            observePeople = ObservePeopleUseCase(
                peopleRepository = PeopleRepository(
                    localDataSource = localPeopleDataSource
                ),
            ),
            deletePerson = DeletePersonUseCase(
                peopleAndNotesRepository = PeopleAndNotesRepository(
                    peopleRepository = PeopleRepository(
                        localDataSource = localPeopleDataSource,
                    ),
                    notesRepository = NotesRepository(
                        localDataSource = localNotesDataSource
                    ),
                ),
            ),
            deletePersonWithNotes = DeletePersonWithNotesUseCase(
                peopleAndNotesRepository = PeopleAndNotesRepository(
                    peopleRepository = PeopleRepository(
                        localDataSource = localPeopleDataSource,
                    ),
                    notesRepository = NotesRepository(
                        localDataSource = localNotesDataSource,
                    ),
                )
            )
        )
    )

    @Test
    fun `combineLatest behaviour`() {
        val flow1 = PublishSubject.create<Boolean>()
        val flow2 = PublishSubject.create<Int>()

        val test = Observable
            .combineLatest(flow1, flow2, ::Pair)
            .test()

        flow1.onNext(true)
        flow2.onNext(1)

        test.assertValue(Pair(true, 1))
    }

    @Test
    fun `relay from optional`() {
        val optional1 = Optional.empty<PersonId>()
        val relay1 = BehaviorRelay.createDefault(optional1)
        val optional2 = Optional.of(PersonId(1))
        val relay2 = BehaviorRelay.createDefault(optional2)

        Observable
            .combineLatest(relay1, relay2, ::Pair)
            .test()
            .assertValues(Pair(optional1, optional2))
    }

    @Test
    fun `emits ui states`() {
        val testSubscriber = sut.state.test()

        localPeopleDataSource.emit(people)

        testSubscriber.assertValuesOnly(
            PeopleUiState(),
            PeopleUiState(
                isLoading = false,
            ),
            PeopleUiState(
                isLoading = false,
                people = people,
            ),
        )
    }

    @Test
    fun `emits ui states v2`() {
        val testSubscriber = sut.state.test()

        testSubscriber.assertValueAt(
            0,
            PeopleUiState(),
        )

        localPeopleDataSource.emit(people)

        testSubscriber.assertValueAt(
            1,
            PeopleUiState(
                isLoading = false,
            )
        )

        testSubscriber.assertValueAt(
            2,
            PeopleUiState(
                isLoading = false,
                people = people,
            )
        )
    }

    @Test
    fun `emits ui states v3`() {
        val testSubscriber = sut.state.test()

        assertThat(testSubscriber.values().single()).isEqualTo(
            PeopleUiState()
        )

        localPeopleDataSource.emit(people)

        assertThat(testSubscriber.values().last()).isEqualTo(
            PeopleUiState(
                isLoading = false,
                people = people,
            )
        )
    }
}
