package dev.hankli.iamstar.ui.home

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Size
import android.view.MenuItem
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import dev.hankli.iamstar.R
import dev.hankli.iamstar.utils.*
import dev.hankli.iamstar.utils.Consts.REQUEST_PICK_MEDIAS
import kotlinx.android.synthetic.main.fragment_edit_post.*
import tw.hankli.brookray.constant.EMPTY

class EditPostFragment : BaseFragment(R.layout.fragment_edit_post), MediaAdapter.Listener {

    override val hasOptionsMenu: Boolean
        get() = true

    override val menuRes: Int
        get() = R.menu.single_action_ok

    private val neededPermissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private val viewModel by viewModels<EditPostViewModel>()

    private val mediaAdapter = MediaAdapter(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        view_list_media.run {
            (this.layoutManager as GridLayoutManager).spanCount = 3
            setHasFixedSize(true)
            adapter = mediaAdapter
        }

        view_add_photos.setOnClickListener {
            askingPermissions(neededPermissions) {
                showMediaPicker(this, REQUEST_PICK_MEDIAS)
            }
        }

        viewModel.mediaItemsData.observe(viewLifecycleOwner, Observer { mediaItems ->
            view_list_media.isVisible = mediaItems.isNotEmpty()

            mediaAdapter.items = mediaItems
            mediaAdapter.notifyDataSetChanged()
        })
    }

    override fun onAllPermissionsGranted() {
        showMediaPicker(this, REQUEST_PICK_MEDIAS)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_PICK_MEDIAS) {
            if (resultCode == RESULT_OK) {
                val selectedMediaItems = obtainResult(data).map { uri ->

                    val type = contentResolver.getType(uri) ?: EMPTY

                    val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        contentResolver.loadThumbnail(uri, Size(96, 96), null)
                    } else {
                        // get video and image thumbnail from uri
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