package dev.hankli.iamstar.ui.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dev.hankli.iamstar.data.enums.Gender
import dev.hankli.iamstar.data.models.Profile
import dev.hankli.iamstar.repo.ProfileRepo
import dev.hankli.iamstar.utils.ArchViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class EditProfileViewModel : ArchViewModel() {

    private lateinit var profile: Profile

    private val profileRepo = ProfileRepo()

    private val _profileData = MutableLiveData<Profile>()
    val profileData: LiveData<Profile>
        get() = _profileData

    fun loadProfile() {
        callProgress(true)
        viewModelScope.launch(IO) {
            val profile = profileRepo.fetchProfile()
            withContext(Main) {
                callProgress(false)
                this@EditProfileViewModel.profile = profile
                refreshProfile()
            }
        }
    }

    fun getBirthday(): Date? = profile.birthday

    fun refreshProfile() {
        _profileData.value = profile
    }

    fun onHeadShotSelected(uri: Uri) {
        callProgress(true)
        viewModelScope.launch {
            profile.photoURL = profileRepo.updateHeadshot(uri)
            withContext(Main) {
                callProgress(false)
                refreshProfile()
            }
        }
    }

    fun onDisplayNameChanged(text: CharSequence?) {
        val displayName = if (text.isNullOrEmpty()) null else text.toString().trimEnd()
        profile.displayName = displayName
    }

    fun onDescriptionChanged(text: CharSequence?) {
        val description = if (text.isNullOrEmpty()) null else text.toString().trimEnd()
        profile.description = description
    }

    fun onFirstNameChanged(text: CharSequence?) {
        val firstName = if (text.isNullOrEmpty()) null else text.toString().trimEnd()
        profile.firstName = firstName
    }

    fun onLastNameChanged(text: CharSequence?) {
        val lastName = if (text.isNullOrEmpty()) null else text.toString().trimEnd()
        profile.lastName = lastName
    }

    fun onBirthdayChanged(milliseconds: Long?) {
        if (milliseconds != null) {
            profile.birthday = Date(milliseconds)
        } else {
            profile.birthday = null
        }
        refreshProfile()
    }

    fun onEmailChanged(text: CharSequence?) {
        val email = if (text.isNullOrEmpty()) null else text.toString().trimEnd()
        profile.email = email
    }

    fun onPhoneNumberChanged(text: CharSequence?) {
        val phoneNumber = if (text.isNullOrEmpty()) null else text.toString().trimEnd()
        profile.phoneNumber = phoneNumber
    }

    // 0 = Male, 1 = Female
    fun onSexChanged(sexType: Int) {
        profile.gender = when (sexType) {
            0 -> Gender.MALE
            1 -> Gender.FEMALE
            else -> error("No such gender !")
        }
        refreshProfile()
    }

    fun submit() {
        callProgress(true)
        viewModelScope.launch(IO) {
            profileRepo.updateProfile(profile)
            withContext(Main) {
                callProgress(false)
                popBack()
            }
        }
    }
}