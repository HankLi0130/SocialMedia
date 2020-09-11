package dev.hankli.iamstar.ui.home

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.hankli.iamstar.R
import dev.hankli.iamstar.data.models.Post
import kotlinx.android.synthetic.main.card_post.view.*
import java.text.SimpleDateFormat

class PostCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    companion object {
        const val layoutRes = R.layout.card_post
    }

    fun bind(position: Int, item: Post) {
        with(itemView) {
            view_head_shot.setImageResource(R.drawable.ic_person)

            item.createdAt?.let {
                view_post_time.text =
                    SimpleDateFormat.getDateInstance(SimpleDateFormat.DATE_FIELD).format(it)
            }

            item.location?.let {
                view_post_location.text = it
            }

            view_post_text.text = item.postText

            view_post_medias.apply {
                this.pageCount = item.postMedia.size
                this.setImageListener { position, imageView ->
                    Glide.with(this@with).load(item.postMedia[position]).into(imageView)
                }
            }

            view_post_reaction_count.text = item.postLikes.toString()

            view_post_comment_count.text = item.postComment.toString()
        }
    }
}