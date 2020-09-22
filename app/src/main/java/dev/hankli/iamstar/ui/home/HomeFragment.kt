package dev.hankli.iamstar.ui.home

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import dev.hankli.iamstar.R
import dev.hankli.iamstar.utils.BaseFragment
import dev.hankli.iamstar.utils.MarginItemDecoration
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : BaseFragment(R.layout.fragment_home) {

    override val hasOptionsMenu: Boolean
        get() = true

    override val menuRes: Int
        get() = R.menu.fragment_home

    private val viewModel by viewModels<HomeViewModel>()

    private lateinit var postCardAdapter: PostCardAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        postCardAdapter = PostCardAdapter()

        view_posts.apply {
            setHasFixedSize(true)
            adapter = postCardAdapter
            addItemDecoration(
                MarginItemDecoration(resources.getDimension(R.dimen.distance_12_dp).toInt())
            )
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, findNavController())
                || super.onOptionsItemSelected(item)
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