package tw.iamstar.utils

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import tw.hankli.brookray.core.constant.NO_RESOURCE
import tw.hankli.brookray.core.event.Event
import tw.iamstar.R
import tw.iamstar.firebase.AuthManager

abstract class ArchViewModel : ViewModel() {

    protected val currentUserId = AuthManager.currentUserId

    private val _viewEvents = MutableLiveData<Event<ViewAction>>()
    val viewEvents: LiveData<Event<ViewAction>>
        get() = _viewEvents

    fun callProgress(show: Boolean) {
        _viewEvents.value = Event(ViewAction.ProgressAction(show))
    }

    fun popBack() {
        _viewEvents.value = Event(ViewAction.PopBackAction)
    }

    fun showAlert(@StringRes messageRes: Int) {
        _viewEvents.value = Event(ViewAction.AlertAction(messageRes))
    }

    fun showError(@StringRes messageRes: Int) {
        _viewEvents.value = Event(ViewAction.ErrorAction(messageRes))
    }

    fun showErrors(messageRes: List<Int>) {
        _viewEvents.value = Event(ViewAction.ErrorsAction(messageRes))
    }

    fun showMessage(@StringRes titleRes: Int = NO_RESOURCE, @StringRes messageRes: Int) {
        _viewEvents.value = Event(ViewAction.MessageAction(titleRes, messageRes))
    }

    fun notifyView(code: Int) {
        _viewEvents.value = Event(ViewAction.CustomAction(code))
    }

    fun showNoInternet() {
        showError(R.string.error_no_internet)
    }
}