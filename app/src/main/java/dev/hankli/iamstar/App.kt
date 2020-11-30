package dev.hankli.iamstar

import android.app.Application
import com.google.android.libraries.places.api.Places
import com.google.firebase.firestore.DocumentReference
import dev.hankli.iamstar.firebase.AuthManager
import dev.hankli.iamstar.firestore.InfluencerManager
import dev.hankli.iamstar.firestore.ProfileManager

class App : Application() {

    lateinit var influencer: DocumentReference
    lateinit var user: DocumentReference

    override fun onCreate() {
        super.onCreate()

        Places.initialize(this, getString(R.string.places_api_key))

        influencer = InfluencerManager.getDoc(getString(R.string.influencer_object_id))
        loadUser()
    }

    fun loadUser() {
        AuthManager.currentUser?.let { user = ProfileManager.getDoc(it.uid) }
    }
}