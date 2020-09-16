package dev.hankli.iamstar.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import dev.hankli.iamstar.R
import dev.hankli.iamstar.utils.BaseFragment
import kotlinx.android.synthetic.main.fragment_edit_post.*

class EditPostFragment : BaseFragment(R.layout.fragment_edit_post) {

    override val hasOptionsMenu: Boolean
        get() = true

    override val menuRes: Int
        get() = R.menu.single_action_ok

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view_add_photos.setOnClickListener {

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

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