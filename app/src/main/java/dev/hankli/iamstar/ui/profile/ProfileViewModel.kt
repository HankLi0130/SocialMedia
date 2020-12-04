package dev.hankli.iamstar.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.ListenerRegistration
import dev.hankli.iamstar.data.models.Profile
import dev.hankli.iamstar.firestore.ProfileManager
import dev.hankli.iamstar.utils.BaseViewModel

class ProfileViewModel : BaseViewModel() {

    private lateinit var registration: ListenerRegistration

    private val _profileData = MutableLiveData<Profile>()
    val profileData: LiveData<Profile>
        get() = _profileData

    fun subscribeProfile(profileId: String) {
        registration = ProfileManager.addSnapshotListener(profileId) { snapshot, exception ->
            if (exception != null) {
                exception.printStackTrace()
                return@addSnapshotListener
            }

            val profile = snapshot?.toObject(Profile::class.java)
            _profileData.value = profile
        }
    }

    override fun onCleared() {
        super.onCleared()
        registration.remove()
    }
}