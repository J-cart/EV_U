package com.tutorials.ev_u

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import com.tutorials.ev_u.databinding.ActivityMainBinding
import com.tutorials.ev_u.util.CURRENT_DESTINATION_ID

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var currentDestination: NavDestination

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putInt(CURRENT_DESTINATION_ID,currentDestination.id)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val destinationId = savedInstanceState?.getInt(CURRENT_DESTINATION_ID,0) ?: 0

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        /*appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
            )
        )*/

        val fragHost = supportFragmentManager.findFragmentById(R.id.frag_host) as NavHostFragment
        navController = fragHost.findNavController()




        navController.addOnDestinationChangedListener { _, destination, _ ->
            currentDestination = destination
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}