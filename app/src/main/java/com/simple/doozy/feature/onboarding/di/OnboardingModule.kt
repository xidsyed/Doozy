package com.simple.doozy.feature.onboarding.di

import com.simple.doozy.feature.onboarding.OnboardingRepository
import com.simple.doozy.feature.onboarding.OnboardingViewModel
import org.koin.dsl.module
import org.koin.plugin.module.dsl.*

val onboardingModule = module{
    single<OnboardingRepository>()
    viewModel<OnboardingViewModel>()
}