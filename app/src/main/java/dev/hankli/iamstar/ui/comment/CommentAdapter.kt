package dev.hankli.iamstar.ui.comment

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import dev.hankli.iamstar.R
import dev.hankli.iamstar.data.models.Comment
import kotlinx.android.synthetic.main.card_comment.view.*
import tw.hankli.brookray.core.extension.viewOf
import java.text.SimpleDateFormat

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
                item.profile?.let { doc ->
                    doc.get().addOnSuccessListener { snapshot ->
                        val url = snapshot.getString("photoURL")
                        if (url.isNullOrEmpty()) view_commenter_head_shot.setImageResource(R.drawable.ic_person)
                        else Glide.with(this).load(url).into(view_commenter_head_shot)

                        val name = snapshot.getString("displayName")
                        view_commenter_name.text = name
                    }
                } ?: view_commenter_head_shot.setImageResource(R.drawable.ic_person)

                view_commenter_name.text = item.commenterName

                view_comment_time.text =
                    SimpleDateFormat.getDateInstance(SimpleDateFormat.DATE_FIELD)
                        .format(item.createdAt)

                view_comment_content.text = item.content
            }
        }
    }
}