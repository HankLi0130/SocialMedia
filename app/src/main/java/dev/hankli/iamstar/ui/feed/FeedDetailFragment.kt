package dev.hankli.iamstar.ui.feed

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import dev.hankli.iamstar.R
import dev.hankli.iamstar.data.enums.ReactionType
import dev.hankli.iamstar.data.models.Feed
import dev.hankli.iamstar.data.models.Media
import dev.hankli.iamstar.ui.comment.CommentAdapter
import dev.hankli.iamstar.utils.ArchFragment
import dev.hankli.iamstar.utils.ArchViewModel
import dev.hankli.iamstar.utils.ext.isInternetConnected
import dev.hankli.iamstar.utils.ext.toDateString
import dev.hankli.iamstar.utils.media.VIDEO
import kotlinx.android.synthetic.main.fragment_feed_detail.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import tw.hankli.brookray.recyclerview.decoration.MarginItemDecoration

class FeedDetailFragment : ArchFragment<ArchViewModel>(R.layout.fragment_feed_detail) {

    override val viewModel: FeedDetailViewModel by viewModel()

    private val args: FeedDetailFragmentArgs by navArgs()

    private lateinit var commentAdapter: CommentAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadFeed(args.feedId, app.influencerId)

        viewModel.feedData.observe(viewLifecycleOwner, { feed -> updateUI(feed) })

        commentAdapter = CommentAdapter(viewModel.getCommentOptions(args.feedId))

        view_comment_list.run {
            adapter = commentAdapter
            addItemDecoration(
                MarginItemDecoration(resources.getDimension(R.dimen.distance_12_dp).toInt())
            )
        }

        view_send.setOnClickListener {
            if (requireContext().isInternetConnected()) {
                val message = view_input_comment.text.toString()
                viewModel.sendComment(args.feedId, message)
                view_input_comment.text.clear()
                view_input_comment.onEditorAction(EditorInfo.IME_ACTION_DONE)
            } else viewModel.showNoInternet()
        }
    }

    private fun updateUI(feed: Feed) {
        feed.photoURL?.let { url ->
            Glide.with(this).load(url).into(view_profile_avatar.image)
        } ?: view_profile_avatar.image.setImageResource(R.drawable.ic_person)

        view_feed_time.text = feed.createdAt.toDateString()

        view_feed_location.text = feed.location
        view_feed_location.isVisible = !feed.location.isNullOrEmpty()

        view_feed_content.text = feed.content

        if (feed.medias.isEmpty()) {
            view_feed_medias.isVisible = false
        } else {
            view_feed_medias.isVisible = true
            view_feed_medias.setImageListener { position, imageView ->
                val media = feed.medias[position]
                view_play.isVisible = media.type == VIDEO
                Glide.with(this).load(media.thumbnailUrl).into(imageView)
            }
            view_feed_medias.pageCount = feed.medias.size
            view_feed_medias.setImageClickListener { position ->
                onMediaClick(feed.medias[position])
            }
        }

        view_feed_reaction.setOnClickListener {
            if (requireContext().isInternetConnected()) {
                viewModel.doReaction(args.feedId)
            } else viewModel.showNoInternet()
        }
        val src = feed.reactionByCurrentUser?.reactionType?.drawableRes
            ?: ReactionType.NO_REACTION.drawableRes
        view_feed_reaction.setImageResource(src)

        view_feed_reaction_count.isVisible = feed.reactionCount > 0
        view_feed_reaction_count.text = feed.reactionCount.toString()

        view_feed_comment_count.isVisible = feed.commentCount > 0
        view_feed_comment_count.text = feed.commentCount.toString()
    }

    override fun notifyFromViewModel(code: Int) {
        if (code == viewModel.refreshCommentsCode) commentAdapter.notifyDataSetChanged()
    }

    override fun onStart() {
        super.onStart()
        commentAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        commentAdapter.stopListening()
    }

    private fun onMediaClick(media: Media) {
        findNavController().navigate(
            FeedDetailFragmentDirections.actionFeedDetailFragmentToMediaReviewFragment(
                media.url,
                media.type,
                media.width,
                media.height
            )
        )
    }
}