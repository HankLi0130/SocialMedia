package dev.hankli.iamstar.ui.comment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import dev.hankli.iamstar.R
import dev.hankli.iamstar.utils.BaseArchFragment

class CommentFragment : BaseArchFragment<CommentViewModel>(R.layout.fragment_comment) {

    override val viewModel: CommentViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}