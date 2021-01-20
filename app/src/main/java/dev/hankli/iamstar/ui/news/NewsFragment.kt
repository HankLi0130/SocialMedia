package dev.hankli.iamstar.ui.news

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import dev.hankli.iamstar.R
import kotlinx.android.synthetic.main.fragment_news.*

class NewsFragment : Fragment(R.layout.fragment_news) {

    private lateinit var newsPageAdapter: NewsPageAdapter

    private val newsUrls = listOf(
        NewsItem(
            "https://www.cdc.gov.tw",
            R.string.news_centers_for_disease_control
        ),
        NewsItem(
            "https://www.cwb.gov.tw/V8/C/",
            R.string.news_central_weather_bureau_weather
        ),
        NewsItem(
            "https://www.cwb.gov.tw/V8/C/E/index.html",
            R.string.news_central_weather_bureau_earthquake
        ),
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newsPageAdapter = NewsPageAdapter(this, newsUrls)
        view_pager.adapter = newsPageAdapter

        TabLayoutMediator(view_tabs, view_pager) { tab, position ->
            tab.setText(newsUrls[position].titleRes)
        }.attach()
    }
}