package dev.hankli.iamstar.ui.signup

import android.app.Application
import android.net.Uri
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import dev.hankli.iamstar.App
import dev.hankli.iamstar.R
import dev.hankli.iamstar.model.UserModel
import dev.hankli.iamstar.utils.Consts.USERS
import dev.hankli.iamstar.utils.FirestoreUtil.auth
import dev.hankli.iamstar.utils.FirestoreUtil.db
import dev.hankli.iamstar.utils.FirestoreUtil.storage

class SignUpViewModel(private val myApplication: Application) : AndroidViewModel(myApplication) {

    var fullName: String = ""
    var phoneNumber: String = ""
    var email: String = ""
    var password: String = ""
    private val _imageUri = MutableLiveData<Uri>().apply { value = null }
    val imageUri: LiveData<Uri>
        get() = _imageUri

    private val _shouldNavigateToHome = MutableLiveData<Boolean>().apply { value = false }
    val shouldNavigateToHome: LiveData<Boolean>
        get() = _shouldNavigateToHome

    private val _loading = MutableLiveData<String>()
    val loading: LiveData<String>
        get() = _loading

    private val _alert = MutableLiveData<String>()
    val alert: LiveData<String>
        get() = _alert


    fun createUser() {
        when {
            fullName.isEmpty() -> _alert.value =
                myApplication.getString(R.string.name_required_error)
            phoneNumber.isEmpty() -> _alert.value =
                myApplication.getString(R.string.phone_required_error)
            email.isEmpty() -> _alert.value =
                myApplication.getString(R.string.email_required_error)
            password.isEmpty() -> _alert.value =
                myApplication.getString(R.string.password_required_error)
            !Patterns.PHONE.matcher(phoneNumber).matches() -> _alert.value =
                myApplication.getString(R.string.malformed_phone_error)
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> _alert.value =
                myApplication.getString(R.string.malformed_email_error)
            password.length < 6 -> _alert.value =
                myApplication.getString(R.string.short_password_error)
            else -> {
                _loading.value = myApplication.getString(R.string.creating_new_user)
                //create user without image

                auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {

                    if (imageUri.value == null) {
                        createUserWithoutImage()
                    } else {
                        createUserWithImage()
                    }
                }.addOnFailureListener {
                    it.printStackTrace()
                    _loading.value = null
                    _alert.value = myApplication.getString(R.string.error_message)
                }
            }
        }
    }

    private fun createUserWithoutImage() {
        val userid = auth.currentUser!!.uid
        val items = HashMap<String, Any>()
        items["email"] = email
        items["firstName"] = fullName
        items["lastName"] = ""
        items["userName"] = ""
        items["phoneNumber"] = phoneNumber
        items["userID"] = userid
        items["profilePictureURL"] = ""
        items["active"] = true
        saveUserToDatabase(auth.currentUser!!, items)
    }

    private fun createUserWithImage() {
        val photoRef = storage.child("images/" + auth.currentUser!!.uid + ".png")
        photoRef.putFile(imageUri.value!!).addOnProgressListener {
        }.continueWithTask { task ->
            if (!task.isSuccessful) {
                throw task.exception!!
            }
            photoRef.downloadUrl
        }.addOnSuccessListener { downloadUri ->

            val userId = auth.currentUser!!.uid
            val items = HashMap<String, Any>()
            items["email"] = email
            items["firstName"] = fullName
            items["lastName"] = ""
            items["userName"] = ""
            items["phoneNumber"] = phoneNumber
            items["userID"] = userId
            items["profilePictureURL"] = downloadUri.toString()
            items["active"] = true
            saveUserToDatabase(auth.currentUser!!, items)
        }
    }

    private fun saveUserToDatabase(user: FirebaseUser, items: HashMap<String, Any>) {
        db.collection(USERS).document(user.uid).set(items)
            .addOnSuccessListener {
                val userModel = UserModel()
                userModel.userID = user.uid
                userModel.email = items["email"].toString()
                userModel.firstName = items["firstName"].toString()
                userModel.lastName = items["lastName"].toString()
                userModel.userName = items["userName"].toString()
                userModel.profilePictureURL = items["profilePictureURL"].toString()
                userModel.active = true
                App.currentUser = userModel
                _loading.value = null
                navigateToHomeFragment()
            }.addOnFailureListener {
                _alert.value = myApplication.getString(R.string.error_message)
                _loading.value = null
                it.printStackTrace()
            }
    }

    fun doneShowingAlert() {
        _alert.value = null
    }

    private fun navigateToHomeFragment() {
        _shouldNavigateToHome.value = true
    }

    fun doneNavigatingToHomeFragment() {
        _shouldNavigateToHome.value = false
    }

    fun setImageUri(uri: Uri) {
        _imageUri.value = uri
    }

    fun showError(message: String) {
        _alert.value = message
    }
}