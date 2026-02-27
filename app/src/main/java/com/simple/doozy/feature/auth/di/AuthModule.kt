package com.simple.doozy.feature.auth.di

import com.simple.doozy.feature.auth.AuthManager
import com.simple.doozy.feature.auth.screens.LoginViewModel
import com.simple.doozy.feature.auth.screens.OtpViewModel
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single
import org.koin.plugin.module.dsl.viewModel


val authModule = module {
    single<AuthManager>()
    viewModel<LoginViewModel>()
    viewModel<OtpViewModel>()
}