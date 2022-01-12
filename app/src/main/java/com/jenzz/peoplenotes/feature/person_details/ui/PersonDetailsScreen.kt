package com.jenzz.peoplenotes.feature.person_details.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import com.jenzz.peoplenotes.common.data.people.PersonId
import com.ramcosta.composedestinations.annotation.Destination

data class PersonDetailsScreenNavArgs(
    val personId: PersonId
)

@Destination(navArgsDelegate = PersonDetailsScreenNavArgs::class)
@Composable
fun PersonDetailsScreen(
    viewModel: PersonDetailsViewModel = hiltViewModel()
) {
    Text(
        modifier = Modifier.fillMaxSize(),
        textAlign = TextAlign.Center,
        text = "Person Details Screen",
    )
}
