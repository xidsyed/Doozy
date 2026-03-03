package com.simple.doozy.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.simple.doozy.DoozyApplication
import com.simple.doozy.MainViewModel
import com.simple.doozy.common.ui.util.SnackbarController
import com.simple.doozy.core.appDataStore
import com.simple.doozy.feature.auth.data.AuthRepository
import com.simple.doozy.feature.auth.data.DefaultAuthRepository
import com.simple.doozy.feature.auth.di.authModule
import com.simple.doozy.feature.onboarding.di.onboardingModule
import com.simple.doozy.feature.session.SessionManager
import com.simple.doozy.feature.todo.di.TodoModule
import com.simple.doozy.feature.user.data.DefaultUserRepository
import com.simple.doozy.feature.user.data.UserRepository
import com.simple.doozy.navigation.HomeViewModel
import kotlinx.coroutines.CoroutineScope
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single
import org.koin.plugin.module.dsl.viewModel

val mainModule = module {
    single<CoroutineScope> {
        val application = androidContext().applicationContext as DoozyApplication
        application.applicationScope
    }
    single<DataStore<Preferences>> { androidContext().appDataStore }
    single<AuthRepository> { DefaultAuthRepository() }
    single<UserRepository> { DefaultUserRepository(get()) }
    single<SessionManager> { SessionManager(get(), get(), get(), get()) }
    single<SnackbarController>()
    viewModel<MainViewModel>()
    viewModel<HomeViewModel>()
    includes(TodoModule)
    includes(authModule)
    includes(onboardingModule)
    includes(com.simple.doozy.feature.profile.di.profileModule)

}