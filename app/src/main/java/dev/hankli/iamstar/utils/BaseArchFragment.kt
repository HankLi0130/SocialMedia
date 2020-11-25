package dev.hankli.iamstar.utils

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes

abstract class BaseArchFragment<T : BaseViewModel> : BaseFragment {

    constructor() : super()

    constructor(@LayoutRes layoutRes: Int) : super(layoutRes)

    protected abstract val viewModel: T

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // UI Event Listeners

    }
}