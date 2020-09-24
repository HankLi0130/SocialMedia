package dev.hankli.iamstar.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.kaopiz.kprogresshud.KProgressHUD
import dev.hankli.iamstar.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var hud: KProgressHUD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val topLevelDestinations = setOf(R.id.homeFragment, R.id.profileFragment)
        appBarConfiguration = AppBarConfiguration(topLevelDestinations)

        navController = findNavController(R.id.view_nav_host)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->

            // Action Bar
            if (destination.id == R.id.authFragment) {
                supportActionBar?.hide()
            } else {
                supportActionBar?.show()
            }

            // Navigation
            view_bottom_nav.isVisible = topLevelDestinations.contains(destination.id)
        }

        // Action Bar
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Bottom Navigation
        view_bottom_nav.setupWithNavController(navController)

        prepareDialogs()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun getNavController(): NavController {
        return findNavController(R.id.view_nav_host)
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

    fun restart() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}