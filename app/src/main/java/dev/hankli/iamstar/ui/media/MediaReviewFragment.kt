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
import kotlinx.android.synthetic.main.fragment_media_review.*

class MediaReviewFragment : Fragment(R.layout.fragment_media_review) {

    private val args: MediaReviewFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when (args.type) {
            IMAGE -> displayImage()
            VIDEO -> displayVideo()
        }
    }

    private fun displayImage() {
        view_image.isVisible = true
        Glide.with(this).load(args.url).into(view_image)
    }

    private fun displayVideo() {

    }
}