package dev.hankli.iamstar.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.firestore.DocumentReference
import dev.hankli.iamstar.MainNavDirections
import dev.hankli.iamstar.R
import dev.hankli.iamstar.firebase.AuthManager
import dev.hankli.iamstar.firestore.InfluencerManager
import dev.hankli.iamstar.firestore.ProfileManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    lateinit var currentUser: DocumentReference
        private set

    val influencer: DocumentReference by lazy {
        InfluencerManager.getDoc(getString(R.string.influencer_object_id))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val topLevelDestinations = setOf(R.id.feedFragment, R.id.profileFragment)
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

        // Auth
        AuthManager.currentUser?.let { user ->
            lifecycleScope.launchWhenCreated {
                currentUser = ProfileManager.getDocByUid(user.uid)
            }
        } ?: navController.navigate(MainNavDirections.actionGlobalAuthFragment())

        // Action Bar
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Bottom Navigation
        view_bottom_nav.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun restart() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}