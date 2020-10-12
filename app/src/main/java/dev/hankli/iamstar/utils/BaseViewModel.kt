package dev.hankli.iamstar.utils

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

abstract class BaseViewModel : ViewModel() {

    protected val disposables = CompositeDisposable()

    // TODO Show progress

    // TODO Show message dialog

    // TODO Popup

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
        disposables.clear()
    }
}