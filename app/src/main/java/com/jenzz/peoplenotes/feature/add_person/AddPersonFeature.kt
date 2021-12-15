package com.jenzz.peoplenotes.feature.add_person

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.jenzz.peoplenotes.feature.Feature
import com.jenzz.peoplenotes.feature.add_person.ui.AddPersonScreen
import com.jenzz.peoplenotes.feature.home.HomeFeature

object AddPersonFeature : Feature {

    override val route: String = "add_person"

    @Composable
    override fun Content(navController: NavHostController) {
        val context = LocalContext.current
        AddPersonScreen(
            onPersonAdded = {
                navController.navigate(HomeFeature.route) {
                    popUpTo(HomeFeature.route) {
                        inclusive = true
                    }
                }
                Toast.makeText(context, "User added.", Toast.LENGTH_LONG).show()
            }
        )
    }
}
