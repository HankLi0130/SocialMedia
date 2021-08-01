package app.hankdev.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import app.hankdev.firebase.FirebaseUserLiveData

class SharedViewModel : ViewModel() {

    enum class AuthenticationState {
        AUTHENTICATED, UNAUTHENTICATED, INVALID_AUTHENTICATION
    }

    val authenticationState = FirebaseUserLiveData().map { user ->
        if (user != null) AuthenticationState.AUTHENTICATED
        else AuthenticationState.UNAUTHENTICATED
    }
}