package dev.hankli.iamstar.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.kaopiz.kprogresshud.KProgressHUD
import dev.hankli.iamstar.R
import dev.hankli.iamstar.utils.FirestoreUtil.auth
import dev.hankli.iamstar.utils.FirestoreUtil.getUserToken

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var hud: KProgressHUD
    private lateinit var alertDialog: AlertDialog
    private lateinit var builder: AlertDialog.Builder
    private var token = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        obtainUserToken()

        navController = findNavController(R.id.navHostFragment)
        val navGraph = navController.graph

        when {
            auth.currentUser == null -> navGraph.startDestination = R.id.authFragment
            else -> {
                navGraph.startDestination = R.id.homeFragment
//                getCurrentUser(OnSuccessListener {
//                    val userModel = it.toObject(UserModel::class.java)!!
//                    userModel.fcmToken = token
//                    App.currentUser = userModel
//                    updateUser(App.currentUser!!) {}
//                }, OnFailureListener {
//                    navGraph.startDestination = R.id.authFragment
//                })
            }
        }

        navController.graph = navGraph

        prepareDialogs()
    }

    private fun obtainUserToken() {
        getUserToken {
            if (!it.isNullOrEmpty()) {
                token = it
            }
        }
    }

    private fun prepareDialogs() {
        hud = KProgressHUD.create(this)
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setDimAmount(0.6f)
            .setCancellable(false)
            .setMaxProgress(100)
    }

    fun showDialog(message: String) {
        builder = AlertDialog.Builder(this)
        builder.setMessage(message)
        builder.setPositiveButton("Ok") { _, _ -> }
        alertDialog = builder.create()
        alertDialog.show()
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