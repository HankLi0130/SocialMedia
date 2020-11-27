package dev.hankli.iamstar.ui.feed

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import dev.hankli.iamstar.R
import dev.hankli.iamstar.data.models.Feed
import kotlinx.android.synthetic.main.card_feed.view.*
import tw.hankli.brookray.extension.viewOf
import java.text.SimpleDateFormat

class FeedCardAdapter(options: FirestoreRecyclerOptions<Feed>) :
    FirestoreRecyclerAdapter<Feed, FeedCardAdapter.ViewHolder>(options) {

    lateinit var onItemOptionsClick: (feedId: String) -> Unit

    lateinit var onItemReactionClick: (feedId: String) -> Unit

    lateinit var onItemCommentClick: (feedId: String) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = parent.viewOf(R.layout.card_feed)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Feed) {
        holder.bind(model, onItemOptionsClick, onItemReactionClick, onItemCommentClick)
        holder.setLike(model.reaction != null)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(
            item: Feed,
            onItemOptionsClick: (feedId: String) -> Unit,
            onItemReactionClick: (feedId: String) -> Unit,
            onItemCommentClick: (feedId: String) -> Unit
        ) {
            with(itemView) {
                view_feed_head_shot.setImageResource(R.drawable.ic_person)

                view_feed_time.text = SimpleDateFormat.getDateInstance(SimpleDateFormat.DATE_FIELD)
                    .format(item.createdAt)

                view_feed_location.text = item.location

                view_feed_content.text = item.content

                if (item.medias.isEmpty()) {
                    view_feed_medias.isVisible = false
                } else {
                    view_feed_medias.isVisible = true
                    view_feed_medias.pageCount = item.medias.size
                    view_feed_medias.setImageListener { position, imageView ->
                        Glide.with(this).load(item.medias[position].thumbnailUrl)
                            .into(imageView)
                    }
                }

                view_feed_reaction.setOnClickListener { onItemReactionClick(item.objectId) }
                view_feed_reaction_count.isVisible = item.reactionCount > 0
                view_feed_reaction_count.text = item.reactionCount.toString()

                view_feed_comment.setOnClickListener { onItemCommentClick(item.objectId) }
                view_feed_comment_count.isVisible = item.commentCount > 0
                view_feed_comment_count.text = item.commentCount.toString()

                view_feed_more_options.setOnClickListener { onItemOptionsClick(item.objectId) }
            }
        }

        fun setLike(like: Boolean) {
            val src = if (like) R.drawable.ic_reaction_like else R.drawable.ic_like
            itemView.view_feed_reaction.setImageResource(src)
        }
    }
}