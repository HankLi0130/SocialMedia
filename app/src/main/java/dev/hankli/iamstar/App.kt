package dev.hankli.iamstar

import android.app.Application
import com.google.android.libraries.places.api.Places
import dev.hankli.iamstar.di.koinModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {

    lateinit var influencerId: String
        private set

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(koinModules)
        }

        Places.initialize(this, getString(R.string.places_api_key))
        influencerId = getString(R.string.influencer_id)
    }
}