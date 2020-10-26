package dev.hankli.iamstar.utils

import android.app.Dialog
import android.app.ProgressDialog
import android.content.ContentResolver
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.annotation.ArrayRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.core.content.PermissionChecker.PERMISSION_DENIED
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dev.hankli.iamstar.R
import dev.hankli.iamstar.ui.MainActivity
import tw.hankli.brookray.constant.NO_RESOURCE
import tw.hankli.brookray.extension.showListDialog
import tw.hankli.brookray.extension.showMessageDialog

abstract class BaseFragment : Fragment {

    constructor() : super()

    constructor(@LayoutRes layoutRes: Int) : super(layoutRes)

    protected open val hasOptionsMenu = false

    protected open val menuRes = NO_RESOURCE

    protected val contentResolver: ContentResolver
        get() = requireContext().contentResolver

    private lateinit var progressDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(hasOptionsMenu)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (menuRes != NO_RESOURCE) inflater.inflate(menuRes, menu)
        super.onCreateOptionsMenu(menu, inflater)
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

    protected fun mainActivity(): MainActivity = requireActivity() as MainActivity

    protected fun showProgressDialog() {
        progressDialog = ProgressDialog(requireContext())
        progressDialog.show()
    }

    protected fun dismissProgressDialog() {
        progressDialog.dismiss()
    }

    protected fun showMessageDialog(
        @StringRes titleId: Int,
        @StringRes messageId: Int,
        cancelable: Boolean = true,
        onSubmit: (() -> Unit)? = null
    ) {
        requireContext().showMessageDialog(titleId, messageId, R.string.ok, cancelable) { _, _ ->
            onSubmit?.invoke()
        }
    }

    protected fun showAlert(
        @StringRes messageId: Int,
        cancelable: Boolean = true,
        onSubmit: (() -> Unit)? = null
    ) {
        requireContext().showMessageDialog(
            R.string.alert_title,
            messageId,
            R.string.ok,
            cancelable
        ) { _, _ ->
            onSubmit?.invoke()
        }
    }

    protected fun showAlert(
        message: String,
        cancelable: Boolean = true,
        onSubmit: (() -> Unit)? = null
    ) {
        requireContext().showMessageDialog(
            getString(R.string.alert_title),
            getString(R.string.alert_message, message),
            getString(R.string.ok),
            cancelable
        ) { _, _ ->
            onSubmit?.invoke()
        }
    }

    protected fun showListDialog(
        @StringRes titleId: Int,
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