package dev.hankli.iamstar.ui.home

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dev.hankli.iamstar.R
import dev.hankli.iamstar.utils.BaseFragment
import dev.hankli.iamstar.utils.MarginItemDecoration
import kotlinx.android.synthetic.main.fragment_home.*
import tw.hankli.brookray.constant.EMPTY
import tw.hankli.brookray.extension.getListDialog

class HomeFragment : BaseFragment(R.layout.fragment_home) {

    override val hasOptionsMenu: Boolean
        get() = true

    override val menuRes: Int
        get() = R.menu.fragment_home

    private val viewModel by viewModels<HomeViewModel>()

    private lateinit var postCardAdapter: PostCardAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        postCardAdapter = PostCardAdapter().apply {
            onItemOptionsClick = this@HomeFragment::showItemOptions
        }

        view_posts.apply {
            setHasFixedSize(true)
            adapter = postCardAdapter
            addItemDecoration(
                MarginItemDecoration(resources.getDimension(R.dimen.distance_12_dp).toInt())
            )
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add_post -> {
                toEditPostFragment()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun toEditPostFragment(objectId: String = EMPTY) {
        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToEditPostFragment(objectId)
        )
    }

    fun showItemOptions(objectId: String) {
        requireContext().getListDialog(
            R.string.post_options_title,
            R.array.post_options
        ) { dialogInterface, index ->
            when (index) {
                0 -> toEditPostFragment(objectId)
                1 -> TODO("Delete this post")
                else -> dialogInterface.cancel()
            }
        }.show()
    }

    override fun onStart() {
        super.onStart()
        postCardAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        postCardAdapter.stopListening()
    }
}