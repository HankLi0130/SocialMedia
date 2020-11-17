package dev.hankli.iamstar.ui.feed

import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentReference
import dev.hankli.iamstar.data.models.Feed
import dev.hankli.iamstar.repo.FeedRepo
import dev.hankli.iamstar.utils.BaseViewModel

class FeedViewModel : BaseViewModel() {

    private val feedRepo: FeedRepo by lazy { FeedRepo() }

    fun deletePost(objectId: String) {
        showProgress()
//        deleteFeed(objectId)
//            .doOnComplete { dismissProgress() }
//            .subscribe({
//
//            }, {
//                showAlert(R.string.alert_delete_post_failed)
//            })
//            .addTo(disposables)
    }

    fun onPostCardReactionClick(objectId: String, reactionType: String, checked: Boolean) {

    }

    fun getOptions(influencer: DocumentReference): FirestoreRecyclerOptions<Feed> {
        val query = feedRepo.getFeeds(influencer)
        return FirestoreRecyclerOptions.Builder<Feed>()
            .setQuery(query, Feed::class.java)
            .build()
    }
}