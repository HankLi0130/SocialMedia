package dev.hankli.iamstar.ui.feed

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import dev.hankli.iamstar.R
import dev.hankli.iamstar.data.models.Feed
import dev.hankli.iamstar.data.models.Media
import dev.hankli.iamstar.data.models.Profile
import dev.hankli.iamstar.firestore.ProfileManager
import dev.hankli.iamstar.ui.comment.CommentAdapter
import dev.hankli.iamstar.utils.ArchFragment
import dev.hankli.iamstar.utils.ArchViewModel
import dev.hankli.iamstar.utils.ext.display
import dev.hankli.iamstar.utils.ext.isInternetConnected
import dev.hankli.iamstar.utils.media.VIDEO
import kotlinx.android.synthetic.main.fragment_comment.*
import kotlinx.android.synthetic.main.fragment_feed_detail.*
import kotlinx.android.synthetic.main.fragment_feed_detail.view_comment_list
import kotlinx.android.synthetic.main.fragment_feed_detail.view_input_comment
import kotlinx.android.synthetic.main.fragment_feed_detail.view_send
import tw.hankli.brookray.recyclerview.decoration.MarginItemDecoration

class FeedDetailFragment : ArchFragment<ArchViewModel>(R.layout.fragment_feed_detail) {

    override val viewModel: FeedDetailViewModel by viewModels()

    private val args: FeedDetailFragmentArgs by navArgs()

    private lateinit var commentAdapter: CommentAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadFeed(args.feedId)

        viewModel.feedData.observe(viewLifecycleOwner, { feed -> updateUI(feed) })

        ProfileManager.getDoc(app.influencerId).get()
            .addOnSuccessListener { snapshot ->
                snapshot.getString(Profile.PHOTO_URL)?.let { url ->
                    Glide.with(this).load(url).into(view_profile_avatar.image)
                } ?: view_profile_avatar.image.setImageResource(R.drawable.ic_person)
            }
            .addOnFailureListener {
                view_profile_avatar.image.setImageResource(R.drawable.ic_person)
            }

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
        view_feed_time.text = feed.createdAt.display()

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

        view_feed_reaction_count.isVisible = feed.reactionCount > 0
        view_feed_reaction_count.text = feed.reactionCount.toString()

        view_feed_comment_count.isVisible = feed.commentCount > 0
        view_feed_comment_count.text = feed.commentCount.toString()
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