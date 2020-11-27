package dev.hankli.iamstar.ui.comment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import dev.hankli.iamstar.R
import dev.hankli.iamstar.data.models.Comment
import dev.hankli.iamstar.firestore.FeedManager
import dev.hankli.iamstar.utils.BaseArchFragment
import kotlinx.android.synthetic.main.fragment_comment.*

class CommentFragment : BaseArchFragment<CommentViewModel>(R.layout.fragment_comment) {

    override val viewModel: CommentViewModel by viewModels()

    private val args: CommentFragmentArgs by navArgs()

    private lateinit var commentAdapter: CommentAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val options = FirestoreRecyclerOptions.Builder<Comment>()
            .setQuery(FeedManager.queryComments(args.feedId)) { snapshot ->
                val comment =
                    snapshot.toObject(Comment::class.java) ?: error("Comment parse failed !")
                return@setQuery comment
            }
            .build()

        commentAdapter = CommentAdapter(options)

        view_comment_list.run {
            setHasFixedSize(true)
            adapter = commentAdapter
        }
    }

    override fun onStart() {
        super.onStart()
        commentAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        commentAdapter.stopListening()
    }
}