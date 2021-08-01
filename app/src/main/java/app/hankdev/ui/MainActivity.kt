package app.hankdev.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import app.hankdev.R

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private val noActionBarDestinations = setOf(R.id.authFragment)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.view_nav_host) as NavHostFragment

        navController = navHostFragment.navController
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            // Action Bar
            if (noActionBarDestinations.contains(destination.id)) {
                supportActionBar?.hide()
            } else {
                supportActionBar?.show()
            }
        }

        appBarConfiguration = AppBarConfiguration(navController.graph)

        // Action Bar
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun restart() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}