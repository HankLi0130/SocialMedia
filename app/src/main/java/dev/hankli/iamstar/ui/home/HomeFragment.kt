package dev.hankli.iamstar.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dev.hankli.iamstar.R
import dev.hankli.iamstar.utils.MarginItemDecoration
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val viewModel by viewModels<HomeViewModel>()

    private val postCardAdapter = PostCardAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view_posts.apply {
            setHasFixedSize(true)
            adapter = postCardAdapter
            addItemDecoration(
                MarginItemDecoration(resources.getDimension(R.dimen.distance_12_dp).toInt())
            )
        }
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