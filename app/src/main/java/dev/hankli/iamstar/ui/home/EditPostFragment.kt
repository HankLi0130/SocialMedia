package dev.hankli.iamstar.ui.home

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import dev.hankli.iamstar.R
import dev.hankli.iamstar.utils.BaseFragment
import dev.hankli.iamstar.utils.Consts.REQUEST_PERMISSION_MEDIA
import dev.hankli.iamstar.utils.Consts.REQUEST_PICK_MEDIAS
import dev.hankli.iamstar.utils.Consts.REQUEST_PLACES
import dev.hankli.iamstar.utils.UIAction
import dev.hankli.iamstar.utils.getPlacesIntent
import dev.hankli.iamstar.utils.media.*
import io.reactivex.Single
import kotlinx.android.synthetic.main.fragment_edit_post.*

class EditPostFragment : BaseFragment(R.layout.fragment_edit_post), MediaAdapter.Listener {

    override val hasOptionsMenu: Boolean
        get() = true

    override val menuRes: Int
        get() = R.menu.single_action_ok

    private val args: EditPostFragmentArgs by navArgs()

    private val viewModel by viewModels<EditPostViewModel>()

    private val mediaAdapter = MediaAdapter(this)

    private val maxImageSelectable = 12

    private val maxVideoSelectable = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel.loadPost(args.postId)

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

        viewModel.contentData.observe(viewLifecycleOwner, Observer { content ->
            view_input_post_text.setText(content)
            view_input_post_text.setSelection(content.length)
        })

        viewModel.mediaItemsData.observe(viewLifecycleOwner, Observer { mediaItems ->
            view_list_media.isVisible = mediaItems.isNotEmpty()

            mediaAdapter.forBrowses = mediaItems
            mediaAdapter.notifyDataSetChanged()
        })

        viewModel.locationData.observe(viewLifecycleOwner, Observer { location ->
            view_post_location.text =
                if (location.isNullOrEmpty()) getText(R.string.location_not_available)
                else location
        })

        viewModel.uiEvents.observe(viewLifecycleOwner, Observer { event ->
            event.getContentIfNotHandle()?.let { action ->
                when (action) {
                    UIAction.SHOW_PROGRESS -> showProgressDialog()
                    UIAction.DISMISS_PROGRESS -> dismissProgressDialog()
                    UIAction.POP_BACK -> popBack()
                }
            }
        })

        viewModel.uiAlertEvents.observe(viewLifecycleOwner, Observer { event ->
            event.getContentIfNotHandle()?.let { messageId ->
                showAlert(messageId)
            }
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
        if (viewModel.isMediaItemsEmpty()) {
            showListDialog(R.string.media_types_title, R.array.media_types) { which ->
                when (which) {
                    0 -> showImagePicker(this, maxImageSelectable, REQUEST_PICK_MEDIAS)
                    1 -> showVideoPicker(this, maxVideoSelectable, REQUEST_PICK_MEDIAS)
                }
            }
        } else {
            val mediaItemCount = viewModel.getMediaItemCount()
            when (viewModel.getMediaItemsType()) {
                IMAGE -> {
                    if (mediaItemCount < maxImageSelectable) {
                        showImagePicker(
                            this,
                            maxImageSelectable - mediaItemCount,
                            REQUEST_PICK_MEDIAS
                        )
                    } else {
                        showMessageDialog(R.string.error_title, R.string.up_to_image_maximum)
                    }
                }
                VIDEO -> {
                    showMessageDialog(R.string.error_title, R.string.up_to_video_maximum)
                }
                else -> showAlert(R.string.alert_unknown_type)
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
            val selectedMediaItems = obtainResult(data).mapNotNull { uri ->
                getMediaItem(contentResolver, uri)
            }
            viewModel.addToMediaItems(selectedMediaItems)
        }
    }

    private fun handlePlaces(resultCode: Int, data: Intent?) {
        when (resultCode) {
            RESULT_OK -> {
                data?.let {
                    val place = Autocomplete.getPlaceFromIntent(it)
                    viewModel.setLocation(
                        place.name,
                        place.latLng?.latitude,
                        place.latLng?.longitude
                    )
                }
            }
            AutocompleteActivity.RESULT_ERROR -> {
                data?.let {
                    val status = Autocomplete.getStatusFromIntent(it)
                    status.statusMessage?.let { message ->
                        showAlert(message)
                    }
                }
            }
            RESULT_CANCELED -> {
                // The user canceled the operation.
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_ok -> {
                viewModel.submit(this::transfer)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onItemCancel(position: Int) {
        viewModel.removeMediaItemAt(position)
    }

    private fun transfer(mediasForBrowse: List<MediaForBrowse>): Single<List<MediaForUpload>> {
        val actions = mediasForBrowse.mapNotNull {
            when (it.type) {
                IMAGE -> imageForUpload(contentResolver, it)
                VIDEO -> videoForUpload(requireContext(), it)
                else -> null
            }
        }

        return Single.merge(actions).toList()
    }
}