package dev.hankli.iamstar.ui.feed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import dev.hankli.iamstar.R
import dev.hankli.iamstar.data.models.Feed
import kotlinx.android.synthetic.main.card_post.view.*
import java.text.SimpleDateFormat

class FeedCardAdapter(options: FirestoreRecyclerOptions<Feed>) :
    FirestoreRecyclerAdapter<Feed, FeedCardAdapter.ViewHolder>(options) {

    lateinit var onItemOptionsClick: (objectId: String) -> Unit

    lateinit var onItemReactionClick: (objectId: String, reactionType: String, isChecked: Boolean) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_post, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holderCard: ViewHolder, position: Int, model: Feed) {
        holderCard.bind(model, onItemOptionsClick, onItemReactionClick)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(
            item: Feed,
            onItemOptionsClick: (objectId: String) -> Unit,
            onItemReactionClick: (objectId: String, reactionType: String, isChecked: Boolean) -> Unit
        ) {
            with(itemView) {
                view_head_shot.setImageResource(R.drawable.ic_person)

                view_post_time.text = item.createdAt?.let {
                    SimpleDateFormat.getDateInstance(SimpleDateFormat.DATE_FIELD).format(it)
                }

                view_post_location.text = item.location

                view_post_text.text = item.content

                if (item.medias.isEmpty()) {
                    view_post_medias.isVisible = false
                } else {
                    view_post_medias.isVisible = true
                    view_post_medias.pageCount = item.medias.size
                    view_post_medias.setImageListener { position, imageView ->
                        Glide.with(this@with).load(item.medias[position].thumbnailUrl)
                            .into(imageView)
                    }
                }

                view_post_reaction.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (buttonView.isPressed) {
                        onItemReactionClick(item.objectId, "like", isChecked)
                    }
                }
                view_post_reaction_count.isVisible = item.reactionCount > 0
                view_post_reaction_count.text = item.reactionCount.toString()

                view_post_comment_count.isVisible = item.commentCount > 0
                view_post_comment_count.text = item.commentCount.toString()

                view_post_more_options.setOnClickListener { onItemOptionsClick(item.objectId) }
            }
        }
    }
}