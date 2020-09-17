package dev.hankli.iamstar.utils

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.annotation.LayoutRes
import androidx.core.content.PermissionChecker.PERMISSION_DENIED
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.fragment.app.Fragment
import dev.hankli.iamstar.utils.Consts.NO_RESOURCE
import dev.hankli.iamstar.utils.Consts.REQUEST_PERMISSION

abstract class BaseFragment : Fragment {

    constructor() : super()

    constructor(@LayoutRes layoutRes: Int) : super(layoutRes)

    protected open val hasOptionsMenu = false

    protected open val menuRes = NO_RESOURCE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(hasOptionsMenu)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (menuRes != NO_RESOURCE) inflater.inflate(menuRes, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    protected fun askingPermissions(permissions: Array<out String>, doOnAllGranted: () -> Unit) {
        val deniedPermissions = getDeniedPermissions(permissions)

        if (deniedPermissions.isEmpty()) doOnAllGranted()
        else requestPermissions(deniedPermissions, REQUEST_PERMISSION)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_PERMISSION) {
            if (!grantResults.contains(PERMISSION_DENIED)) onAllPermissionsGranted()
        }
    }

    protected open fun onAllPermissionsGranted() {
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
}