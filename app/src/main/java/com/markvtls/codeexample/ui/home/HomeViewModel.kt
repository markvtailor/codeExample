package com.wallpaperscraft.keby.app.ui.home

import androidx.lifecycle.ViewModel
import com.wallpaperscraft.keby.domain.usecases.settings.SharedDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val sharedDataUseCase: SharedDataUseCase
) : ViewModel() {

    fun checkIfFirstLaunch(): Boolean {
        return sharedDataUseCase.checkIfFirstLaunch()
    }

    fun saveFirstLaunch() {
        sharedDataUseCase.saveFirstLaunch()
    }

}