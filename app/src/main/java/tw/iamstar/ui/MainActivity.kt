package tw.iamstar.ui

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
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinApiExtension
import tw.iamstar.NavGraphDirections
import tw.iamstar.R
import tw.iamstar.firebase.AuthManager
import tw.iamstar.firebase.NotificationManager

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private val notificationManager: NotificationManager by inject()

    private val topLevelDestinations = setOf(
        R.id.feedFragment,
        R.id.newsFragment,
        R.id.scheduleFragment,
        R.id.profileFragment
    )

    private val noActionBarDestinations = setOf(R.id.authFragment)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appBarConfiguration = AppBarConfiguration(topLevelDestinations)

        navController = findNavController(R.id.view_nav_host)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->

            // Action Bar
            if (noActionBarDestinations.contains(destination.id)) {
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
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    @KoinApiExtension
    override fun onStart() {
        super.onStart()
        if (AuthManager.currentUser == null) {
            navController.navigate(NavGraphDirections.actionGlobalAuthFragment())
        } else {
            notificationManager.uploadFcmToken(this, this)
        }
    }

    fun restart() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}