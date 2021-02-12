package dev.hankli.iamstar.ui.feed

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import dev.hankli.iamstar.R
import dev.hankli.iamstar.utils.ArchFragment
import dev.hankli.iamstar.utils.Consts.REQUEST_PERMISSION_MEDIA
import dev.hankli.iamstar.utils.Consts.REQUEST_PICK_MEDIAS
import dev.hankli.iamstar.utils.Consts.REQUEST_PLACES
import dev.hankli.iamstar.utils.ext.isInternetConnected
import dev.hankli.iamstar.utils.getPlacesIntent
import dev.hankli.iamstar.utils.media.*
import kotlinx.android.synthetic.main.fragment_edit_feed.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditFeedFragment :
    ArchFragment<EditFeedViewModel>(R.layout.fragment_edit_feed, R.menu.single_action_ok),
    MediaFileAdapter.Listener {

    override val viewModel: EditFeedViewModel by viewModel()

    private val args: EditFeedFragmentArgs by navArgs()

    private val mediaAdapter = MediaFileAdapter(this)

    private val maxImageSelectable = 6

    private val maxVideoSelectable = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadFeed(args.feedId)

        lifecycleScope.launchWhenCreated {
            viewModel.getPhotoURL(app.influencerId)?.let { url ->
                Glide.with(this@EditFeedFragment).load(url).into(view_feed_head_shot)
            } ?: view_feed_head_shot.setImageResource(R.drawable.ic_person)
        }

        view_list_media.run {
            (this.layoutManager as GridLayoutManager).spanCount = 3
            setHasFixedSize(true)
            adapter = mediaAdapter
        }

        view_input_post_text.doOnTextChanged { text, _, _, _ ->
            viewModel.onContentChanged(text)
        }

        view_add_photos.setOnClickListener {
            askingPermissions(mediaPickerPermissions, REQUEST_PERMISSION_MEDIA)
        }

        view_add_location.setOnClickListener {
            //askingPermissions(placesPermission, REQUEST_PERMISSION_PLACES)
            startActivityForResult(getPlacesIntent(requireContext()), REQUEST_PLACES)
        }

        viewModel.contentData.observe(viewLifecycleOwner, { content ->
            view_input_post_text.setText(content)
            view_input_post_text.setSelection(content.length)
        })

        viewModel.mediaFilesData.observe(viewLifecycleOwner, { mediaFiles ->
            view_list_media.isVisible = mediaFiles.isNotEmpty()

            mediaAdapter.items = mediaFiles
            mediaAdapter.notifyDataSetChanged()
        })

        viewModel.locationData.observe(viewLifecycleOwner, { location ->
            view_post_location.text = location
        })
    }

    override fun onAllPermissionsGranted(requestCode: Int) {
        when (requestCode) {
            REQUEST_PERMISSION_MEDIA -> selectTypeOfMedia()
//            REQUEST_PERMISSION_PLACES -> startActivityForResult(
//                getPlacesIntent(requireContext()),
//                REQUEST_PLACES
//            )
        }
    }

    private fun selectTypeOfMedia() {
        if (viewModel.isMediaFilesEmpty()) {
            showListDialog(itemsId = R.array.media_types) { which ->
                when (which) {
                    0 -> showImagePicker(this, maxImageSelectable, REQUEST_PICK_MEDIAS)
                    1 -> showVideoPicker(this, maxVideoSelectable, REQUEST_PICK_MEDIAS)
                }
            }
        } else {
            when (viewModel.getMediaFilesType()) {
                IMAGE -> {
                    val mediaItemCount = viewModel.getMediaFilesCount()
                    if (mediaItemCount < maxImageSelectable) {
                        showImagePicker(
                            this,
                            maxImageSelectable - mediaItemCount,
                            REQUEST_PICK_MEDIAS
                        )
                    } else {
                        showMessageDialog(
                            getString(R.string.error_title),
                            getString(R.string.up_to_image_maximum, maxImageSelectable)
                        )
                    }
                }
                VIDEO -> {
                    showMessageDialog(
                        getString(R.string.error_title),
                        getString(R.string.up_to_video_maximum, maxVideoSelectable)
                    )
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_PICK_MEDIAS -> handleMedias(resultCode, data)
            REQUEST_PLACES -> handlePlaces(resultCode, data)
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun handleMedias(resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            val uris = obtainResult(data)
            val paths = obtainPathResult(data)
            val selectedMediaFiles = toMediaFiles(
                requireContext().contentResolver,
                uris,
                paths
            )

            viewModel.addMediaFiles(selectedMediaFiles)
        }
    }

    private fun handlePlaces(resultCode: Int, data: Intent?) {
        when (resultCode) {
            RESULT_OK -> {
                data?.let {
                    Autocomplete.getPlaceFromIntent(it).run {
                        viewModel.setLocation(
                            name,
                            latLng?.latitude,
                            latLng?.longitude
                        )
                    }
                }
            }
            AutocompleteActivity.RESULT_ERROR -> {
                // TODO handle error
//                data?.let {
//                    val status = Autocomplete.getStatusFromIntent(it)
//                    status.statusMessage?.let { message -> }
//                }
            }
            RESULT_CANCELED -> {
                // The user canceled the operation.
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_ok -> {
                if (requireContext().isInternetConnected()) {
                    viewModel.submit(requireContext().contentResolver, app.influencerId)
                } else viewModel.showNoInternet()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onItemCancel(position: Int) = viewModel.removeMediaItemAt(position)
}