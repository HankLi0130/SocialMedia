package app.hankdev.ui.feed

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import app.hankdev.R
import app.hankdev.data.enums.ReactionType
import app.hankdev.data.models.firestore.Feed
import app.hankdev.data.models.firestore.Reaction
import app.hankdev.utils.media.VIDEO
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.android.synthetic.main.card_feed.view.*
import tw.hankli.brookray.core.extension.viewOf

class FeedCardAdapter(options: FirestoreRecyclerOptions<Feed>) :
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
            onItemClick,
            onItemOptionsClick,
            onItemReactionClick,
            onItemCommentClick
        )
        //holder.setReaction(model.reactionByCurrentUser)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(
            item: Feed,
            onItemClick: (feedId: String) -> Unit,
            onItemOptionsClick: (feedId: String) -> Unit,
            onItemReactionClick: (feedId: String) -> Unit,
            onItemCommentClick: (feedId: String) -> Unit
        ) {
            with(itemView) {
                setOnClickListener { onItemClick(item.objectId) }

                item.photoURL?.let { url ->
                    Glide.with(this).load(url).into(view_user_avatar.image)
                } ?: view_user_avatar.image.setImageResource(R.drawable.ic_person)
                view_user_name.text = "Hank"

                //view_feed_time.text = item.createdAt.toDateString()
                view_feed_time.text = "1 hour age"

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

                view_feed_more_options.setOnClickListener { onItemOptionsClick(item.objectId) }
            }
        }

        fun setReaction(reaction: Reaction?) {
            val src = reaction?.reactionType?.drawableRes
                ?: ReactionType.NONE.drawableRes
            itemView.view_feed_reaction.setImageResource(src)
        }
    }
}