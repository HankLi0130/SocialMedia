package dev.hankli.iamstar.ui.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dev.hankli.iamstar.data.models.Feed
import dev.hankli.iamstar.repo.FeedRepo
import dev.hankli.iamstar.utils.ArchViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FeedDetailViewModel : ArchViewModel() {

    private val feedRepo = FeedRepo()

    fun loadFeed(feedId: String): LiveData<Feed> {
        val liveData = MutableLiveData<Feed>()
        viewModelScope.launch(Main) {
            callProgress(true)
            withContext(IO) {
                val feed = feedRepo.fetchFeed(feedId)
                liveData.postValue(feed)
            }
            callProgress(false)
        }
        return liveData
    }
}