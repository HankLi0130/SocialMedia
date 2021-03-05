package tw.iamstar.utils

import androidx.annotation.StringRes

sealed class ViewAction {

    data class ProgressAction(val show: Boolean) : ViewAction()

    object PopBackAction : ViewAction()

    data class MessageAction(
        @StringRes val titleRes: Int,
        @StringRes val messageRes: Int
    ) : ViewAction()

    data class AlertAction(@StringRes val messageRes: Int) : ViewAction()

    data class ErrorAction(@StringRes val messageRes: Int) : ViewAction()

    data class ErrorsAction(val messageRes: List<Int>) : ViewAction()

    data class CustomAction(val code: Int) : ViewAction()
}