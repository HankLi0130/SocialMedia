package dev.hankli.iamstar.utils

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import tw.hankli.brookray.constant.NO_RESOURCE
import tw.hankli.brookray.event.Event

abstract class BaseViewModel : ViewModel() {

    protected val disposables = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    private val _viewEvents = MutableLiveData<Event<ViewAction>>()
    val viewEvents: LiveData<Event<ViewAction>>
        get() = _viewEvents

    protected fun showProgress() {
        _viewEvents.value = Event(ViewAction.ProgressAction(true))
    }

    protected fun dismissProgress() {
        _viewEvents.value = Event(ViewAction.ProgressAction(false))
    }

    protected fun popBack() {
        _viewEvents.value = Event(ViewAction.PopBackAction)
    }

    protected fun showAlert(@StringRes messageRes: Int) {
        _viewEvents.value = Event(ViewAction.AlertAction(messageRes))
    }

    protected fun showMessage(@StringRes titleRes: Int = NO_RESOURCE, @StringRes messageRes: Int) {
        _viewEvents.value = Event(ViewAction.MessageAction(titleRes, messageRes))
    }
}