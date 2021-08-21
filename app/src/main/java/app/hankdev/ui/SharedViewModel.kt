package app.hankdev.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import app.hankdev.data.enums.AuthenticationState
import app.hankdev.firebase.FirebaseUserLiveData

class SharedViewModel : ViewModel() {

    val authenticationState = FirebaseUserLiveData().map { user ->
        if (user != null) AuthenticationState.AUTHENTICATED
        else AuthenticationState.UNAUTHENTICATED
    }
}