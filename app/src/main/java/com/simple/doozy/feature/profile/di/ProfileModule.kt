package com.simple.doozy.feature.profile.di

import com.simple.doozy.feature.profile.EditProfileViewModel
import com.simple.doozy.feature.profile.ProfileViewModel
import com.simple.doozy.feature.subscription.data.DefaultSubscriptionRepository
import com.simple.doozy.feature.subscription.data.SubscriptionRepository
import org.koin.dsl.module
import org.koin.plugin.module.dsl.viewModel

val profileModule = module {
    single<SubscriptionRepository> { DefaultSubscriptionRepository() }
    viewModel<ProfileViewModel>()
    viewModel<EditProfileViewModel>()
}
