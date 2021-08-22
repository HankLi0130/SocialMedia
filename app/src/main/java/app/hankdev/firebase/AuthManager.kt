package app.hankdev.firebase

import android.content.Context
import android.content.Intent
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class AuthManager(private val auth: FirebaseAuth) {
    val currentUser: FirebaseUser?
        get() = auth.currentUser

    val currentUserId: String?
        get() = currentUser?.uid

    fun getSignInIntent(): Intent {
        val idpConfigs = listOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.PhoneBuilder().setDefaultCountryIso("tw").build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.FacebookBuilder().build()
        )
        return AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(idpConfigs)
            .build()
    }

    suspend fun signOut(context: Context) = AuthUI.getInstance().signOut(context).await()
}