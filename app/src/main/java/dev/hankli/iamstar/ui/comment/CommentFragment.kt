package dev.hankli.iamstar.ui.comment

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.navigation.fragment.navArgs
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import dev.hankli.iamstar.R
import dev.hankli.iamstar.data.models.Comment
import dev.hankli.iamstar.utils.ArchFragment
import kotlinx.android.synthetic.main.fragment_comment.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import tw.hankli.brookray.recyclerview.decoration.MarginItemDecoration

class CommentFragment : ArchFragment<CommentViewModel>(R.layout.fragment_comment) {

    override val viewModel: CommentViewModel by viewModel()

    private val args: CommentFragmentArgs by navArgs()

    private lateinit var commentAdapter: CommentAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val options = FirestoreRecyclerOptions.Builder<Comment>()
            .setQuery(viewModel.queryComments(args.feedId)) { snapshot ->
                val comment =
                    snapshot.toObject(Comment::class.java) ?: error("Comment parse failed !")
                return@setQuery comment
            }
            .build()

        commentAdapter = CommentAdapter(options)

        view_comment_list.run {
            setHasFixedSize(true)
            adapter = commentAdapter
            addItemDecoration(
                MarginItemDecoration(resources.getDimension(R.dimen.distance_12_dp).toInt())
            )
        }

        view_send.setOnClickListener {
            val message = view_input_comment.text.toString()
            viewModel.sendComment(args.feedId, message)
            view_input_comment.text.clear()
            view_input_comment.onEditorAction(EditorInfo.IME_ACTION_DONE)
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