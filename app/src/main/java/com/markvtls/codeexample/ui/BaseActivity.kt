package com.wallpaperscraft.keby.app.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.wallpaperscraft.keby.R
import com.wallpaperscraft.keby.app.ui.views.BottomNavBarExtendedView
import com.wallpaperscraft.keby.databinding.ActivityBaseBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BaseActivity : AppCompatActivity() {

    private var _binding: ActivityBaseBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityBaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.navigation.setNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment, R.id.settingsFragment, R.id.favoritesFragment -> binding.navigation.visibility =
                    View.VISIBLE

                else -> binding.navigation.visibility = View.GONE
            }
        }

        if (intent != null && intent.getBooleanExtra(INTENT_EXTRA_MOVE_TO_SETTINGS, false)) {
            binding.navigation.manageDirections(BottomNavBarExtendedView.Companion.PossibleDirections.SETTINGS_SCREEN)
        }
    }

    companion object {
        const val INTENT_EXTRA_MOVE_TO_SETTINGS = "move_to_settings"
    }
}