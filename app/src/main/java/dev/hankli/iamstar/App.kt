package dev.hankli.iamstar

import android.app.Application
import com.google.android.libraries.places.api.Places

class App : Application() {

    lateinit var influencerId: String
        private set

    override fun onCreate() {
        super.onCreate()

        Places.initialize(this, getString(R.string.places_api_key))
        influencerId = getString(R.string.influencer_id)
    }
}