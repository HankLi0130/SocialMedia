package dev.hankli.iamstar.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dev.hankli.iamstar.R
import dev.hankli.iamstar.data.models.Profile
import dev.hankli.iamstar.firestore.ProfileManager
import dev.hankli.iamstar.utils.BaseViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditProfileViewModel : BaseViewModel() {

    private val _profileData = MutableLiveData<Profile>()
    val profileData: LiveData<Profile>
        get() = _profileData

    fun loadProfile(objectId: String) {
        callProgress(true)
        viewModelScope.launch(IO) {
            val profile = ProfileManager.get(objectId)
            withContext(Main) {
                callProgress(false)
                if (profile == null) {
                    showError(R.string.error_unknown)
                    return@withContext
                }
                _profileData.value = profile
            }
        }
    }
}