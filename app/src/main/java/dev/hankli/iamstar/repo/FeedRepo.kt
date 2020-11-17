package dev.hankli.iamstar.repo

import com.google.firebase.firestore.Query
import dev.hankli.iamstar.firestore.FeedManager
import dev.hankli.iamstar.firestore.InfluencerManager

class FeedRepo {
    fun getFeeds(influencerId: String): Query {
        return FeedManager.getQuery(InfluencerManager.get(influencerId))
    }
}