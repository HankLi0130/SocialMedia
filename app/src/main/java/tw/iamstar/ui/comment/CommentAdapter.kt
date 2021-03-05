package tw.iamstar.ui.comment

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.android.synthetic.main.card_comment.view.*
import tw.hankli.brookray.core.extension.viewOf
import tw.iamstar.R
import tw.iamstar.data.models.Comment
import tw.iamstar.utils.ext.toDateString

class CommentAdapter(options: FirestoreRecyclerOptions<Comment>) :
    FirestoreRecyclerAdapter<Comment, CommentAdapter.ViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = parent.viewOf(R.layout.card_comment)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Comment) {
        holder.bind(model)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: Comment) {
            with(itemView) {

                item.photoURL?.let { url ->
                    Glide.with(this).load(url).into(view_commenter_avatar.image)
                } ?: view_commenter_avatar.image.setImageResource(R.drawable.ic_person)

                view_commenter_name.text = item.userName

                view_comment_time.text = item.createdAt.toDateString()

                view_comment_content.text = item.content
            }
        }
    }
}