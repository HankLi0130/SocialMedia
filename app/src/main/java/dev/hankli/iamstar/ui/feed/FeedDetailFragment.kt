package dev.hankli.iamstar.ui.feed

import androidx.fragment.app.viewModels
import dev.hankli.iamstar.R
import dev.hankli.iamstar.utils.ArchFragment
import dev.hankli.iamstar.utils.ArchViewModel

class FeedDetailFragment : ArchFragment<ArchViewModel>(R.layout.fragment_feed_detail) {

    override val viewModel: ArchViewModel by viewModels()
}