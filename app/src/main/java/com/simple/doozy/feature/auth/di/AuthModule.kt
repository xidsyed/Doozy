package com.simple.doozy.feature.auth.di

import com.simple.doozy.feature.auth.data.AuthRepository
import com.simple.doozy.feature.auth.data.DefaultAuthRepository
import com.simple.doozy.feature.auth.screens.LoginViewModel
import com.simple.doozy.feature.auth.screens.OtpViewModel
import org.koin.dsl.module
import org.koin.plugin.module.dsl.viewModel


val authModule = module {
    single<AuthRepository> { DefaultAuthRepository() }
    viewModel<LoginViewModel>()
    viewModel<OtpViewModel>()
}