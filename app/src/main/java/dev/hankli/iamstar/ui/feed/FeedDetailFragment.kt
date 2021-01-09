package dev.hankli.iamstar.ui.feed

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import dev.hankli.iamstar.R
import dev.hankli.iamstar.data.models.Media
import dev.hankli.iamstar.firestore.ProfileManager
import dev.hankli.iamstar.utils.ArchFragment
import dev.hankli.iamstar.utils.ArchViewModel
import dev.hankli.iamstar.utils.ext.display
import dev.hankli.iamstar.utils.media.VIDEO
import kotlinx.android.synthetic.main.fragment_feed_detail.*

class FeedDetailFragment : ArchFragment<ArchViewModel>(R.layout.fragment_feed_detail) {

    override val viewModel: FeedDetailViewModel by viewModels()

    private val args: FeedDetailFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadFeed(args.feedId).observe(viewLifecycleOwner, { feed ->

            view_feed_time.text = feed.createdAt.display()

            if (feed.location.isNullOrEmpty()) {
                view_feed_location.text = null
                view_feed_location.isVisible = false
            } else {
                view_feed_location.text = feed.location
                view_feed_location.isVisible = true
            }

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
                //onItemReactionClick(item.objectId)
            }

            view_feed_reaction_count.isVisible = feed.reactionCount > 0
            view_feed_reaction_count.text = feed.reactionCount.toString()

            view_feed_comment.setOnClickListener {
                //onItemCommentClick(item.objectId)
            }
            view_feed_comment_count.isVisible = feed.commentCount > 0
            view_feed_comment_count.text = feed.commentCount.toString()

        })

        ProfileManager.getDoc(app.influencerId).get()
            .addOnSuccessListener { snapshot ->
                val url = snapshot.getString("photoURL")
                if (url.isNullOrEmpty()) view_profile_avatar.image.setImageResource(R.drawable.ic_person)
                else Glide.with(this).load(url).into(view_profile_avatar.image)
            }
            .addOnFailureListener {
                view_profile_avatar.image.setImageResource(R.drawable.ic_person)
            }
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