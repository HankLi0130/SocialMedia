package tw.iamstar.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.ListenerRegistration
import tw.iamstar.data.models.Profile
import tw.iamstar.repo.ProfileRepo
import tw.iamstar.utils.ArchViewModel

class ProfileViewModel(private val profileRepo: ProfileRepo) : ArchViewModel() {

    private lateinit var registration: ListenerRegistration

    private val _profileData = MutableLiveData<Profile>()
    val profileData: LiveData<Profile>
        get() = _profileData

    fun subscribeProfile() {
        registration = profileRepo.observeProfile(currentUserId!!) { snapshot, exception ->
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
}