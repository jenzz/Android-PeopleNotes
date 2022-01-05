package com.jenzz.peoplenotes

import android.app.Application
import android.os.StrictMode
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PeopleNotesApp : Application() {

    init {
        StrictMode.enableDefaults()
    }
}
