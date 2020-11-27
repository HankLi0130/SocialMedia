package dev.hankli.iamstar.utils

import androidx.annotation.StringRes

sealed class ViewAction {

    data class ProgressAction(val show: Boolean) : ViewAction()

    object PopBackAction : ViewAction()

    data class MessageAction(
        @StringRes val titleRes: Int,
        @StringRes val messageRes: Int
    ) : ViewAction()

    data class AlertAction(@StringRes val messageRes: Int) : ViewAction()
}