package app.hankdev.ui.timeline

import app.hankdev.data.models.firestore.Feed
import app.hankdev.repo.FeedRepo
import app.hankdev.utils.ArchViewModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class TimelineViewModel(private val feedRepo: FeedRepo) : ArchViewModel() {

    fun getFeedOptions() = FirestoreRecyclerOptions.Builder<Feed>()
        .setQuery(feedRepo.queryAll(), Feed::class.java)
        .build()
}