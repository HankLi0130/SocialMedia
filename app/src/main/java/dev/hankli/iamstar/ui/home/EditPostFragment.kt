package dev.hankli.iamstar.ui.home

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.MenuItem
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import dev.hankli.iamstar.R
import dev.hankli.iamstar.utils.*
import dev.hankli.iamstar.utils.Consts.REQUEST_PERMISSION_MEDIA
import dev.hankli.iamstar.utils.Consts.REQUEST_PICK_MEDIAS
import dev.hankli.iamstar.utils.Consts.REQUEST_PLACES
import kotlinx.android.synthetic.main.fragment_edit_post.*
import tw.hankli.brookray.constant.EMPTY

class EditPostFragment : BaseFragment(R.layout.fragment_edit_post), MediaAdapter.Listener {

    override val hasOptionsMenu: Boolean
        get() = true

    override val menuRes: Int
        get() = R.menu.single_action_ok

    private val args: EditPostFragmentArgs by navArgs()

    private val viewModel by viewModels<EditPostViewModel>()

    private val mediaAdapter = MediaAdapter(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel.loadPost(args.postId) { post ->
            view_post_location.text =
                if (post.location.isNullOrEmpty()) getString(R.string.location_not_available) else post.location

            view_input_post_text.setText(post.content)

            // TODO set photos
        }

        view_list_media.run {
            (this.layoutManager as GridLayoutManager).spanCount = 3
            setHasFixedSize(true)
            adapter = mediaAdapter
        }

        view_add_photos.setOnClickListener {
            askingPermissions(mediaPickerPermissions, REQUEST_PERMISSION_MEDIA)
        }

        view_add_location.setOnClickListener {
            //askingPermissions(placesPermission, REQUEST_PERMISSION_PLACES)
            startActivityForResult(getPlacesIntent(requireContext()), REQUEST_PLACES)
        }

        viewModel.mediaItemsData.observe(viewLifecycleOwner, Observer { mediaItems ->
            view_list_media.isVisible = mediaItems.isNotEmpty()

            mediaAdapter.items = mediaItems
            mediaAdapter.notifyDataSetChanged()
        })
    }

    override fun onAllPermissionsGranted(requestCode: Int) {
        when (requestCode) {
            REQUEST_PERMISSION_MEDIA -> showMediaPicker(this, REQUEST_PICK_MEDIAS)
//            REQUEST_PERMISSION_PLACES -> startActivityForResult(
//                getPlacesIntent(requireContext()),
//                REQUEST_PLACES
//            )
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
            val selectedMediaItems = obtainResult(data).map { uri ->

                val type = contentResolver.getType(uri) ?: EMPTY

                // get video and image thumbnail from uri
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentResolver.loadThumbnail(uri, Size(96, 96), null)
                } else {
                    when {
                        type.contains("image") -> {
                            MediaStore.Images.Thumbnails.getThumbnail(
                                contentResolver,
                                uri.lastPathSegment!!.toLong(),
                                MediaStore.Images.Thumbnails.MICRO_KIND,
                                BitmapFactory.Options()
                            )
                        }
                        type.contains("video") -> {
                            MediaStore.Video.Thumbnails.getThumbnail(
                                contentResolver,
                                uri.lastPathSegment!!.toLong(),
                                MediaStore.Video.Thumbnails.MICRO_KIND,
                                BitmapFactory.Options()
                            )
                        }
                        else -> {
                            BitmapFactory.decodeResource(resources, R.drawable.ic_broken_image)
                        }
                    }
                }
                MediaItem(uri, type, bitmap)
            }

            viewModel.addToMediaItems(selectedMediaItems)
        }
    }

    private fun handlePlaces(resultCode: Int, data: Intent?) {
        when (resultCode) {
            RESULT_OK -> {
                data?.let {
                    val place = Autocomplete.getPlaceFromIntent(it)
                    view_post_location.text = place.name
                    Log.i("test", "Place: ${place.name}, ${place.id}")
                }
            }
            AutocompleteActivity.RESULT_ERROR -> {
                data?.let {
                    val status = Autocomplete.getStatusFromIntent(it)
                    status.statusMessage?.let { message ->
                        showDialog(requireContext(), message)
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
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onItemCancel(position: Int) {
        viewModel.removeMediaItemAt(position)
    }
}