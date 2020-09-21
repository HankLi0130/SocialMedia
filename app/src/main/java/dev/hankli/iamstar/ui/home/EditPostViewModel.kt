package dev.hankli.iamstar.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.hankli.iamstar.utils.MediaItem

class EditPostViewModel : ViewModel() {

    private val mediaItems = mutableListOf<MediaItem>()

    private val _mediaItemsData = MutableLiveData<List<MediaItem>>()
    val mediaItemsData: LiveData<List<MediaItem>>
        get() = _mediaItemsData

    fun addToMediaItems(list: List<MediaItem>) {
        mediaItems.addAll(list)
        _mediaItemsData.value = mediaItems
    }

    fun removeMediaItemAt(position: Int) {
        mediaItems.removeAt(position)
        _mediaItemsData.value = mediaItems
    }
}