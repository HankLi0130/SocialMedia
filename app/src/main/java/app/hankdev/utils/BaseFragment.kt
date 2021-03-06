package app.hankdev.utils

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.annotation.ArrayRes
import androidx.annotation.LayoutRes
import androidx.annotation.MenuRes
import androidx.annotation.StringRes
import androidx.core.content.PermissionChecker.PERMISSION_DENIED
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import app.hankdev.R
import app.hankdev.ui.MainActivity
import tw.hankli.brookray.core.constant.NO_RESOURCE
import tw.hankli.brookray.core.extension.showListDialog
import tw.hankli.brookray.core.extension.showMessageDialog
import tw.hankli.brookray.ui.dialog.ProcessDialog

abstract class BaseFragment : Fragment {

    constructor() : super()

    constructor(@LayoutRes layoutRes: Int) : super(layoutRes)

    constructor(
        @LayoutRes layoutId: Int,
        @MenuRes menuRes: Int = NO_RESOURCE
    ) : super(layoutId) {
        this.menuRes = menuRes
    }

    @MenuRes
    protected var menuRes = NO_RESOURCE

    protected val app: app.hankdev.App
        get() = (requireActivity().application) as app.hankdev.App

    protected val mainActivity: MainActivity
        get() = requireActivity() as MainActivity

    private lateinit var processDialog: DialogFragment
    private var isProcessShowing = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        processDialog = ProcessDialog.newInstance(getString(R.string.loading))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(menuRes != NO_RESOURCE)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (menuRes != NO_RESOURCE) inflater.inflate(menuRes, menu)
    }

    protected fun askingPermissions(permissions: Array<out String>, requestCode: Int) {
        val deniedPermissions = getDeniedPermissions(permissions)

        if (deniedPermissions.isEmpty()) onAllPermissionsGranted(requestCode)
        else requestPermissions(deniedPermissions, requestCode)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (!grantResults.contains(PERMISSION_DENIED)) onAllPermissionsGranted(requestCode)
    }

    protected open fun onAllPermissionsGranted(requestCode: Int) {
        /* override it when you need */
    }

    private fun getDeniedPermissions(permissions: Array<out String>): Array<out String> {
        if (permissions.isEmpty()) return emptyArray()

        val deniedPermissions = mutableListOf<String>()

        for (permission in permissions) {
            if (checkSelfPermission(requireContext(), permission) == PERMISSION_DENIED) {
                deniedPermissions.add(permission)
            }
        }

        return deniedPermissions.toTypedArray()
    }

    protected fun callProgressDialog(show: Boolean) {
        isProcessShowing = if (show) {
            if (!isProcessShowing) processDialog.show(parentFragmentManager, null)
            true
        } else {
            processDialog.dismiss()
            false
        }
    }

    protected fun showMessageDialog(
        @StringRes titleId: Int = NO_RESOURCE,
        @StringRes messageId: Int,
        cancelable: Boolean = true,
        onSubmit: (() -> Unit)? = null
    ) {
        requireContext().showMessageDialog(titleId, messageId, R.string.ok, cancelable) { _, _ ->
            onSubmit?.invoke()
        }
    }

    protected fun showMessageDialog(
        title: String? = null,
        message: String,
        cancelable: Boolean = true,
        onSubmit: (() -> Unit)? = null
    ) {
        requireContext().showMessageDialog(
            title,
            message,
            getString(R.string.ok),
            cancelable
        ) { _, _ ->
            onSubmit?.invoke()
        }
    }

    protected fun showMessagesDialog(
        @StringRes titleId: Int = NO_RESOURCE,
        messageIds: List<Int>,
        cancelable: Boolean = true,
        onSubmit: (() -> Unit)? = null
    ) {
        val title = if (titleId == NO_RESOURCE) null else getString(titleId)
        val message = messageIds.joinToString("\n") { getString(it) }
        val buttonText = getString(R.string.ok)
        requireContext().showMessageDialog(title, message, buttonText, cancelable) { _, _ ->
            onSubmit?.invoke()
        }
    }

    protected fun showListDialog(
        @StringRes titleId: Int = NO_RESOURCE,
        @ArrayRes itemsId: Int,
        cancelable: Boolean = true,
        onSubmit: ((which: Int) -> Unit)? = null
    ) {
        requireContext().showListDialog(titleId, itemsId, cancelable) { _, which ->
            onSubmit?.invoke(which)
        }
    }

    protected fun popBack(): Boolean = findNavController().popBackStack()
}