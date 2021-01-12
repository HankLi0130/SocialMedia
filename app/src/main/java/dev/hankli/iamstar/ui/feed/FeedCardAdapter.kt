package dev.hankli.iamstar.ui.feed

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import dev.hankli.iamstar.R
import dev.hankli.iamstar.data.enums.ReactionType
import dev.hankli.iamstar.data.models.Feed
import dev.hankli.iamstar.data.models.Media
import dev.hankli.iamstar.data.models.Reaction
import dev.hankli.iamstar.firestore.ProfileManager
import dev.hankli.iamstar.utils.ext.display
import dev.hankli.iamstar.utils.media.VIDEO
import kotlinx.android.synthetic.main.card_feed.view.*
import tw.hankli.brookray.core.extension.viewOf

class FeedCardAdapter(val showItemOptions: Boolean, options: FirestoreRecyclerOptions<Feed>) :
    FirestoreRecyclerAdapter<Feed, FeedCardAdapter.ViewHolder>(options) {

    lateinit var onItemClick: (feedId: String) -> Unit

    lateinit var onItemOptionsClick: (feedId: String) -> Unit

    lateinit var onItemReactionClick: (feedId: String) -> Unit

    lateinit var onItemCommentClick: (feedId: String) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = parent.viewOf(R.layout.card_feed)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Feed) {
        holder.bind(
            model,
            showItemOptions,
            onItemClick,
            onItemOptionsClick,
            onItemReactionClick,
            onItemCommentClick
        )
        holder.setReaction(model.reaction)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(
            item: Feed,
            showItemOptions: Boolean,
            onItemClick: (feedId: String) -> Unit,
            onItemOptionsClick: (feedId: String) -> Unit,
            onItemReactionClick: (feedId: String) -> Unit,
            onItemCommentClick: (feedId: String) -> Unit
        ) {
            with(itemView) {
                setOnClickListener { onItemClick(item.objectId) }

                item.influencer?.let { doc ->
                    ProfileManager.getDoc(doc.id).get().addOnSuccessListener { snapshot ->
                        val url = snapshot.getString("photoURL")
                        if (url.isNullOrEmpty()) view_profile_avatar.image.setImageResource(R.drawable.ic_person)
                        else Glide.with(this).load(url).into(view_profile_avatar.image)
                    }
                } ?: view_profile_avatar.image.setImageResource(R.drawable.ic_person)

                view_feed_time.text = item.createdAt.display()

                if (item.location.isNullOrEmpty()) {
                    view_feed_location.text = null
                    view_feed_location.isVisible = false
                } else {
                    view_feed_location.text = item.location
                    view_feed_location.isVisible = true
                }

                view_feed_content.text = item.content

                if (item.medias.isEmpty()) {
                    view_feed_media.isVisible = false
                } else {
                    view_feed_media.isVisible = true
                    val media = item.medias[0]
                    Glide.with(this).load(media.thumbnailUrl).into(view_feed_media)
                    view_play.isVisible = media.type == VIDEO
                }

                view_feed_reaction.setOnClickListener { onItemReactionClick(item.objectId) }

                view_feed_reaction_count.isVisible = item.reactionCount > 0
                view_feed_reaction_count.text = item.reactionCount.toString()

                view_feed_comment.setOnClickListener { onItemCommentClick(item.objectId) }
                view_feed_comment_count.isVisible = item.commentCount > 0
                view_feed_comment_count.text = item.commentCount.toString()

                view_feed_more_options.isVisible = showItemOptions
                view_feed_more_options.setOnClickListener { onItemOptionsClick(item.objectId) }
            }
        }

        fun setReaction(reaction: Reaction?) {
            val src = when (reaction?.reactionType) {
                ReactionType.LIKE -> R.drawable.ic_reaction_like
                else -> R.drawable.ic_like
            }
            itemView.view_feed_reaction.setImageResource(src)
        }
    }
}