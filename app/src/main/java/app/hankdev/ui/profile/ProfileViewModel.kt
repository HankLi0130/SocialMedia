package app.hankdev.ui.profile

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import app.hankdev.data.models.firestore.Profile
import app.hankdev.firebase.AuthManager
import app.hankdev.repo.AuthRepo
import app.hankdev.repo.ProfileRepo
import app.hankdev.utils.ArchViewModel
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val profileRepo: ProfileRepo,
    private val authRepo: AuthRepo,
    private val authManager: AuthManager
) : ArchViewModel() {

    val restartCode = 1

    private lateinit var registration: ListenerRegistration

    private val _profileData = MutableLiveData<Profile>()
    val profileData: LiveData<Profile>
        get() = _profileData

    fun subscribeProfile() {
        registration =
            profileRepo.observeProfile(authManager.currentUserId!!) { snapshot, exception ->
                if (exception != null) {
                    exception.printStackTrace()
                    return@observeProfile
                }

                val profile = snapshot?.toObject(Profile::class.java)!!
                _profileData.value = profile
            }
    }

    override fun onCleared() {
        super.onCleared()
        registration.remove()
    }

    fun signOut(context: Context) {
        viewModelScope.launch(Main) {
            callProgress(true)
            authRepo.signOut(context)
            callProgress(false)
            notifyView(restartCode)
        }
    }
}