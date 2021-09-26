package app.hankdev.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class SharedViewModel(private val firebaseAuth: FirebaseAuth) : ViewModel(),
    FirebaseAuth.AuthStateListener {

    private val _authState = MutableSharedFlow<FirebaseUser?>()
    val authState: SharedFlow<FirebaseUser?>
        get() = _authState

    fun checkAuthState() {
        viewModelScope.launch {
            _authState.emit(firebaseAuth.currentUser)
        }
    }

    init {
        firebaseAuth.addAuthStateListener(this)
    }

    override fun onAuthStateChanged(firebaseAuth: FirebaseAuth) {
        checkAuthState()
    }

    override fun onCleared() {
        super.onCleared()
        firebaseAuth.removeAuthStateListener(this)
    }
}