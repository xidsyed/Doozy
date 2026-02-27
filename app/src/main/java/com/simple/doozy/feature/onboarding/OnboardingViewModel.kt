package com.simple.doozy.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val onboardingRepo: OnboardingRepository
) : ViewModel() {

    fun setOnboardingComplete() {
        viewModelScope.launch {
            onboardingRepo.setOnBoardingCompleted(true)
        }
    }

}