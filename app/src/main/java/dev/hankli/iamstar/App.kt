package dev.hankli.iamstar

import android.app.Application
import com.google.android.libraries.places.api.Places

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        Places.initialize(this, getString(R.string.places_api_key))
    }
}