package dev.hankli.iamstar.ui.media

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import dev.hankli.iamstar.R
import dev.hankli.iamstar.utils.media.IMAGE
import dev.hankli.iamstar.utils.media.VIDEO
import kotlinx.android.synthetic.main.dialog_bottom_sheet_media.*

class MediaFragment : Fragment(R.layout.dialog_bottom_sheet_media) {

    private val args: MediaFragmentArgs by navArgs()

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
        if (args.type == VIDEO) initializePlayer()
    }

    override fun onPause() {
        super.onPause()
        if (args.type == VIDEO) view_video.pause()
    }

    override fun onStop() {
        super.onStop()
        if (args.type == VIDEO) releasePlayer()
    }

    private fun initializePlayer() {
        view_video.isVisible = true
        view_video.setVideoPath(args.url)
        view_video.setOnPreparedListener { view_video.start() }
    }

    private fun releasePlayer() {
        view_video.stopPlayback();
    }
}