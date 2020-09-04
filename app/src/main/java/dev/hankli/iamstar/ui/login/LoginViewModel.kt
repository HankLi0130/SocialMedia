package dev.hankli.iamstar.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

class LoginViewModel(private val myApplication: Application) : AndroidViewModel(myApplication) {

    private val _shouldNavigateToHome = MutableLiveData<Boolean>().apply { value = false }
    val shouldNavigateToHome: LiveData<Boolean>
        get() = _shouldNavigateToHome

    private val _loading = MutableLiveData<String>()
    val loading: LiveData<String>
        get() = _loading

    private val _alert = MutableLiveData<String>()
    val alert: LiveData<String>
        get() = _alert

    fun loginWithCredentials(email: String, password: String) {
//        if (email.isEmpty() || password.isEmpty()) {
//            _alert.value = myApplication.getString(R.string.fields_are_required)
//        } else {
//            _loading.value = myApplication.getString(R.string.loading)
//            auth.signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        FirestoreUtil.getCurrentUser(
//                            OnSuccessListener {
//                                _loading.value = null
//                                val userInfo = it.toObject(UserModel::class.java)
//                                MApplication.currentUser = userInfo
//                                MApplication.currentUser!!.active = true
//                                FirestoreUtil.updateUser(MApplication.currentUser!!) {}
//                                Prefs.getInstance(myApplication.applicationContext)?.userId =
//                                    userInfo?.userID
//                                navigateToHomeFragment()
//                            },
//                            OnFailureListener {
//                                it.printStackTrace()
//                                _loading.value = null
//                                _alert.value =
//                                    myApplication.getString(R.string.failed_to_authenticate)
//                            })
//                    } else {
//                        task.exception!!.printStackTrace()
//                        _loading.value = null
//                        _alert.value =
//                            myApplication.getString(R.string.failed_to_authenticate)
//                    }
//                }
//        }
    }

    fun handleFacebookLogin(result: LoginResult) {
//        val credential =
//            FacebookAuthProvider.getCredential(result.accessToken.token.toString())
//        auth.signInWithCredential(credential).addOnCompleteListener { authResult ->
//            if (authResult.isSuccessful) {
//                FirestoreUtil.getUserByID(
//                    authResult.result?.user?.uid!!,
//                    OnCompleteListener {
//                        if (it.isSuccessful) {
//                            val document = it.result
//
//                            if (document!!["email"] != null) {
//                                _loading.value = null
//                                val userInfo = document.toObject(UserModel::class.java)
//                                MApplication.currentUser = userInfo
//                                navigateToHomeFragment()
//                            } else {
//                                createUserFromFacebook(result)
//                            }
//                        }
//                    }
//                )
//            }
//        }
    }

    private fun createUserFromFacebook(result: LoginResult) {

//        val userId = auth.currentUser!!.uid
//
//        val request = GraphRequest.newMeRequest(result.accessToken) { `object`, _ ->
//            try {
//                val picture: JSONObject = `object`.get("picture") as JSONObject
//                val data: JSONObject = picture.getJSONObject("data")
//                val url = data.getString("url")
//                val userInfo = UserModel()
//                userInfo.userID = userId
//                userInfo.email = `object`.get("email").toString()
//                userInfo.firstName = `object`.get("name").toString()
//                userInfo.profilePictureURL = url
//                userInfo.active = true
//                db.collection(USERS).document(userId).set(userInfo).addOnSuccessListener {
//                    MApplication.currentUser = userInfo
//                    _loading.value = null
//                    navigateToHomeFragment()
//                }.addOnFailureListener {
//                    _alert.value = myApplication.getString(R.string.error_message)
//                    it.printStackTrace()
//                    _loading.value = null
//                }
//
//            } catch (e: Exception) {
//                _loading.value = null
//                _alert.value = myApplication.getString(R.string.error_message)
//                e.printStackTrace()
//            }
//        }
//
//        val parameters = Bundle()
//        parameters.putString("fields", "name,email,id,picture.type(large)")
//        request.parameters = parameters
//        request.executeAsync()

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

    fun handleGoogleLogin(googleAccount: GoogleSignInAccount) {
//        _loading.value = myApplication.getString(R.string.loading)
//        val credential = GoogleAuthProvider.getCredential(googleAccount.idToken, null)
//        auth.signInWithCredential(credential).addOnCompleteListener { authResult ->
//            if (authResult.isSuccessful) {
//                FirestoreUtil.getUserByID(
//                    authResult.result?.user?.uid!!,
//                    OnCompleteListener {
//                        if (it.isSuccessful) {
//                            val document = it.result
//                            if (document!!["email"] != null) {
//                                _loading.value = null
//                                val userInfo = document.toObject(UserModel::class.java)
//                                MApplication.currentUser = userInfo
//                                navigateToHomeFragment()
//                            } else {
//                                createUserFromGoogle(googleAccount)
//                            }
//                        }
//                    }
//                )
//            }
//        }

    }

    private fun createUserFromGoogle(googleAccount: GoogleSignInAccount) {
//        val userId = auth.currentUser!!.uid
//        try {
//            val userModel = UserModel()
//            userModel.email = googleAccount.email.toString()
//            userModel.firstName = googleAccount.displayName.toString()
//            userModel.userID = userId
//            userModel.profilePictureURL = googleAccount.photoUrl.toString()
//            userModel.active = true
//            db.collection(USERS).document(userId).set(userModel).addOnSuccessListener {
//                MApplication.currentUser = userModel
//                _loading.value = null
//                navigateToHomeFragment()
//            }.addOnFailureListener {
//                _alert.value = myApplication.getString(R.string.error_message)
//                it.printStackTrace()
//                _loading.value = null
//            }
//        } catch (e: Exception) {
//            _alert.value = myApplication.getString(R.string.error_message)
//            e.printStackTrace()
//            _loading.value = null
//        }
    }
}