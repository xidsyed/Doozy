package com.simple.doozy.feature.profile.di

import com.simple.doozy.feature.profile.AccountPrivacyViewModel
import com.simple.doozy.feature.profile.DeleteAccountUseCase
import com.simple.doozy.feature.profile.EditProfileViewModel
import com.simple.doozy.feature.profile.ProfileViewModel
import com.simple.doozy.feature.profile.SupportViewModel
import com.simple.doozy.feature.subscription.checkout.CheckoutViewModel
import com.simple.doozy.feature.subscription.checkout.DefaultPaymentRepository
import com.simple.doozy.feature.subscription.checkout.PaymentRepository
import com.simple.doozy.feature.subscription.checkout.PaymentViewModel
import com.simple.doozy.feature.subscription.data.DefaultSubscriptionRepository
import com.simple.doozy.feature.subscription.data.SubscriptionRepository
import com.simple.doozy.feature.subscription.status.SubscriptionStatusViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import org.koin.plugin.module.dsl.viewModel

val profileModule = module {
    single<SubscriptionRepository> { DefaultSubscriptionRepository() }
    single<PaymentRepository> { DefaultPaymentRepository(get()) }

    factoryOf(::DeleteAccountUseCase)

    viewModel<ProfileViewModel>()
    viewModel<EditProfileViewModel>()
    viewModel<AccountPrivacyViewModel>()
    viewModel<SupportViewModel>()
    viewModel<PaymentViewModel>()
    viewModel<CheckoutViewModel>()
    viewModel<SubscriptionStatusViewModel>()
}
