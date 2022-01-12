package com.jenzz.peoplenotes.feature.person_details.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.jenzz.peoplenotes.feature.destinations.PersonDetailsScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PersonDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val personId = PersonDetailsScreenDestination.argsFrom(savedStateHandle)
}