package tw.iamstar.ui.media

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import kotlinx.android.synthetic.main.fragment_media_review.*
import tw.iamstar.R
import tw.iamstar.utils.media.IMAGE
import tw.iamstar.utils.media.VIDEO

class MediaReviewFragment : Fragment(R.layout.fragment_media_review) {

    private val args: MediaReviewFragmentArgs by navArgs()

    private var player: SimpleExoPlayer? = null
    private var playWhenReady: Boolean = true
    private var currentWindow = 0
    private var playbackPosition = 0L

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (args.type == IMAGE) displayImage()
    }

    private fun displayImage() {
        view_image.isVisible = true
        Glide.with(this).load(args.url).into(view_image)
    }

    override fun onStart() {
        super.onStart()
        initializePlayer()
    }

    private fun initializePlayer() {
        if (args.type != VIDEO) return

        view_video.isVisible = true
        player = SimpleExoPlayer.Builder(requireContext()).build().apply {
            setMediaItem(MediaItem.fromUri(args.url))
            playWhenReady = this@MediaReviewFragment.playWhenReady
            seekTo(
                this@MediaReviewFragment.currentWindow,
                this@MediaReviewFragment.playbackPosition
            )
            prepare()
        }
        view_video.player = player
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    private fun releasePlayer() {
        if (args.type != VIDEO) return

        player?.let {
            playWhenReady = it.playWhenReady
            playbackPosition = it.currentPosition
            currentWindow = it.currentWindowIndex
            it.release()
        }
        player = null
    }
}