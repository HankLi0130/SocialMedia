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
import java.util.*

class EditProfileViewModel : BaseViewModel() {

    val birthdayChangedCode = 100
    val sexChangedCode = 101

    private lateinit var profile: Profile

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
                this@EditProfileViewModel.profile = profile
                _profileData.value = profile
            }
        }
    }

    fun getBirthday(): Date? = profile.birthday

    fun getSex(): String? = profile.sex

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
        notifyView(birthdayChangedCode)
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
        profile.sex = when (sexType) {
            0 -> "MALE"
            1 -> "FEMALE"
            else -> null
        }
        notifyView(sexChangedCode)
    }

    fun submit() {
        callProgress(true)
        viewModelScope.launch(IO) {
            profile.updatedAt = Date()
            ProfileManager.update(profile)
            withContext(Main) {
                callProgress(false)
                popBack()
            }
        }
    }
}