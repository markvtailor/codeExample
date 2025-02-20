package com.wallpaperscraft.keby.app.ui.views

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.wallpaperscraft.keby.R
import com.wallpaperscraft.keby.databinding.LayoutBottomNavBarExtendedBinding

class BottomNavBarExtendedView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var _binding: LayoutBottomNavBarExtendedBinding? = null
    private val binding get() = _binding!!

    private var currentScreen: PossibleDirections? = null

    private var navController: NavController? = null

    init {
        _binding =
            LayoutBottomNavBarExtendedBinding.inflate(LayoutInflater.from(context), this, true)

        binding.toMain.setOnClickListener { manageDirections(PossibleDirections.MAIN_SCREEN) }
        binding.toSettings.setOnClickListener { manageDirections(PossibleDirections.SETTINGS_SCREEN) }
        binding.toFavorites.setOnClickListener { manageDirections(PossibleDirections.FAVORITES_SCREEN) }
        binding.toMain.callOnClick()
    }

    fun setNavController(outerNavController: NavController) {
        navController = outerNavController
    }

    private fun enableNewDirection(button: NavBarItemView) {

        ObjectAnimator.ofArgb(
            button, "navItemBackgroundColor", ContextCompat.getColor(context, R.color.green)
        ).apply {
            duration = 300
            start()
        }

        ObjectAnimator.ofFloat(button, "translationY", 0F).apply {
            duration = 200
            start()
        }

        ObjectAnimator.ofFloat(
            button, "navIconSize", resources.getDimension(R.dimen.nav_bar_icon_small)
        ).apply {
            duration = 200
            start()
        }

        button.setNavItemIconColor(ContextCompat.getColor(context, R.color.black_2))
    }

    private fun disableCurrentDirection() {

        val button = when (currentScreen) {
            PossibleDirections.MAIN_SCREEN -> binding.toMain
            PossibleDirections.SETTINGS_SCREEN -> binding.toSettings
            PossibleDirections.FAVORITES_SCREEN -> binding.toFavorites
            null -> null
        }

        ObjectAnimator.ofArgb(
            button, "navItemBackgroundColor", ContextCompat.getColor(context, R.color.black_2)
        ).apply {
            duration = 300
            start()
        }

        ObjectAnimator.ofFloat(button, "translationY", 24F).apply {
            duration = 200
            start()
        }

        ObjectAnimator.ofFloat(
            button, "navIconSize", resources.getDimension(R.dimen.nav_bar_icon_large)
        ).apply {
            duration = 200
            start()
        }

        button?.setNavItemIconColor(ContextCompat.getColor(context, R.color.white))
    }

    fun manageDirections(direction: PossibleDirections) {
        if (direction != currentScreen) {
            when (direction) {
                PossibleDirections.MAIN_SCREEN -> {
                    enableNewDirection(binding.toMain)
                }

                PossibleDirections.SETTINGS_SCREEN -> {
                    enableNewDirection(binding.toSettings)
                }

                PossibleDirections.FAVORITES_SCREEN -> {
                    enableNewDirection(binding.toFavorites)
                }
            }
            disableCurrentDirection()
            currentScreen = direction
            navController?.navigate(direction.destination)
        }
    }


    companion object {
        enum class PossibleDirections(val destination: Int) {
            MAIN_SCREEN(R.id.action_global_homeFragment), SETTINGS_SCREEN(R.id.action_global_settingsFragment), FAVORITES_SCREEN(
                R.id.action_global_favoritesFragment
            )
        }
    }

}

