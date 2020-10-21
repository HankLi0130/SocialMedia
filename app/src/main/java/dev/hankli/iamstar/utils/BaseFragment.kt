package dev.hankli.iamstar.utils

import android.app.Dialog
import android.app.ProgressDialog
import android.content.ContentResolver
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.annotation.ArrayRes
import androidx.annotation.LayoutRes
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

    protected lateinit var progressDialog: Dialog

    protected lateinit var messageDialog: Dialog

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
        title: String? = null,
        cancelable: Boolean = true,
        message: String,
        onSubmit: (() -> Unit)? = null
    ) {
        requireContext().showMessageDialog(title, message, cancelable, R.string.ok) { _, _ ->
            onSubmit?.invoke()
        }
    }

    protected fun showListDialog(
        title: String? = null,
        cancelable: Boolean = true,
        @ArrayRes itemsId: Int,
        onSubmit: ((which: Int) -> Unit)? = null
    ) {
        requireContext().showListDialog(title, cancelable, itemsId) { _, which ->
            onSubmit?.invoke(which)
        }
    }

    protected fun popBack(): Boolean = findNavController().popBackStack()
}