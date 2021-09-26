package app.hankdev.utils

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.annotation.MenuRes
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import app.hankdev.R
import app.hankdev.ui.SharedViewModel
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import tw.hankli.brookray.core.constant.NO_RESOURCE

abstract class ArchFragment<T : ArchViewModel> : BaseFragment {

    constructor() : super()

    constructor(@LayoutRes layoutRes: Int) : super(layoutRes)

    constructor(
        @LayoutRes layoutId: Int,
        @MenuRes menuRes: Int = NO_RESOURCE
    ) : super(layoutId, menuRes)

    protected abstract val viewModel: T

    private val sharedViewModel: SharedViewModel by sharedViewModel()

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

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            sharedViewModel.authState.collect { currentUser ->

                if (currentUser == null) {
                    if (findNavController().currentDestination?.id != R.id.authFragment) {
                        findNavController().navigate(R.id.action_global_authFragment)
                    }
                } else {
                    whenUserReady(currentUser)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        sharedViewModel.checkAuthState()
    }

    protected open fun notifyFromViewModel(code: Int) {}

    protected open fun whenUserReady(currentUser: FirebaseUser) {}
}