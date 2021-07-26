package app.hankdev.ui.web

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import app.hankdev.R
import kotlinx.android.synthetic.main.fragment_web.*

class WebFragment : Fragment(R.layout.fragment_web) {

    companion object {
        private const val URL = "url"
        private const val JAVASCRIPT_ENABLE = "javascriptEnable"

        fun newInstance(url: String, javascriptEnable: Boolean = false) = WebFragment().apply {
            val args = Bundle().apply {
                putString(URL, url)
                putBoolean(JAVASCRIPT_ENABLE, javascriptEnable)
            }
            this.arguments = args
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.getString(URL)?.let {
            view_web.loadUrl(it)

            val enable = arguments?.getBoolean(JAVASCRIPT_ENABLE) ?: false
            view_web.settings.javaScriptEnabled = enable
        }
    }
}