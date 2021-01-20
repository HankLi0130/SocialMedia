package dev.hankli.iamstar.ui.news

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import dev.hankli.iamstar.ui.web.WebFragment

class NewsPageAdapter(fragment: Fragment, private val newsUrls: List<NewsItem>) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount() = newsUrls.size

    override fun createFragment(position: Int): Fragment {
        return WebFragment.newInstance(newsUrls[position].url, true)
    }
}