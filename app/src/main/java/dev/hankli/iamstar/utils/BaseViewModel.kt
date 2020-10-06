package dev.hankli.iamstar.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {

    private val _progressData = MutableLiveData<Boolean>()
    val progressData: LiveData<Boolean>
        get() = _progressData

    fun showProgress(toShow: Boolean = true) {
        _progressData.value = toShow
    }
}