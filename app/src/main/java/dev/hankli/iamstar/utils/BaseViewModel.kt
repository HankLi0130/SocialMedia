package dev.hankli.iamstar.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import tw.hankli.brookray.event.Event

abstract class BaseViewModel : ViewModel() {

    protected val disposables = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
        disposables.clear()
    }

    private val _uiEvents = MutableLiveData<Event<UIAction>>()
    val uiEvents: LiveData<Event<UIAction>>
        get() = _uiEvents

    protected fun showProgress() {
        _uiEvents.value = Event(UIAction.SHOW_PROGRESS)
    }

    protected fun dismissProgress() {
        _uiEvents.value = Event(UIAction.DISMISS_PROGRESS)
    }

    // TODO Show message dialog

    protected fun popBack() {
        _uiEvents.value = Event(UIAction.POP_BACK)
    }
}