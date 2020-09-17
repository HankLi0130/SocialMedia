package dev.hankli.iamstar.ui.home

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import dev.hankli.iamstar.R
import dev.hankli.iamstar.utils.BaseFragment
import dev.hankli.iamstar.utils.Consts.REQUEST_PICK_MEDIAS
import dev.hankli.iamstar.utils.obtainResult
import dev.hankli.iamstar.utils.showMediaPicker
import kotlinx.android.synthetic.main.fragment_edit_post.*

class EditPostFragment : BaseFragment(R.layout.fragment_edit_post) {

    override val hasOptionsMenu: Boolean
        get() = true

    override val menuRes: Int
        get() = R.menu.single_action_ok

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view_add_photos.setOnClickListener {
            showMediaPicker(this, REQUEST_PICK_MEDIAS)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_PICK_MEDIAS) {
            if (resultCode == RESULT_OK) {
                val results = obtainResult(data)
                Log.i("test", results.toString())
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
}