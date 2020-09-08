package dev.hankli.iamstar.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.kaopiz.kprogresshud.KProgressHUD
import dev.hankli.iamstar.R
import dev.hankli.iamstar.utils.FirebaseUtil.auth

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var hud: KProgressHUD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navController = findNavController(R.id.navHostFragment)

        if (auth.currentUser != null) {
            // already signed in
            val user = auth.currentUser!!
            Log.i("test", "user id: ${user.uid}")
        } else {
            // not signed in
            val navGraph = navController.graph
            navGraph.startDestination = R.id.authFragment
            navController.graph = navGraph
        }

        prepareDialogs()
    }

    private fun prepareDialogs() {
        hud = KProgressHUD.create(this)
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setDimAmount(0.6f)
            .setCancellable(false)
            .setMaxProgress(100)
    }

    fun showProgress(message: String) {
        hud.setLabel(message)
        hud.show()
    }

    fun hideProgress() {
        hud.dismiss()
    }

    fun checkPermissions(code: Int): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val read =
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            val write =
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            val camera =
                checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
            return if (!read || !write || !camera) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                    ), code
                )
                false
            } else {
                true
            }
        } else {
            return true
        }
    }
}