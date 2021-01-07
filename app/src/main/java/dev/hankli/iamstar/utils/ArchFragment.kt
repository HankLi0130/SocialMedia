package dev.hankli.iamstar.utils

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import dev.hankli.iamstar.R

abstract class ArchFragment<T : ArchViewModel> : BaseFragment {

    constructor() : super()

    constructor(@LayoutRes layoutRes: Int) : super(layoutRes)

    protected abstract val viewModel: T

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.viewEvents.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandle()?.let { action ->
                when (action) {
                    is ViewAction.ProgressAction -> callProgressDialog(action.show)
                    is ViewAction.PopBackAction -> popBack()
                    is ViewAction.MessageAction -> showMessageDialog(
                        action.titleRes,
                        action.messageRes
                    )
                    is ViewAction.AlertAction -> showMessageDialog(
                        R.string.alert_title,
                        action.messageRes
                    )
                    is ViewAction.ErrorAction -> showMessageDialog(
                        R.string.error_title,
                        action.messageRes
                    )
                    is ViewAction.ErrorsAction -> showMessagesDialog(
                        R.string.error_title,
                        action.messageRes
                    )
                    is ViewAction.CustomAction -> notifyFromViewModel(action.code)
                }
            }
        }
    }

    protected open fun notifyFromViewModel(code: Int) {}
}