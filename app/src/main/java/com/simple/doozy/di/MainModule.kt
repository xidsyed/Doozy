package com.simple.doozy.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.simple.doozy.DoozyApplication
import com.simple.doozy.MainViewModel
import com.simple.doozy.core.appDataStore
import com.simple.doozy.feature.auth.AuthManager
import com.simple.doozy.feature.auth.di.authModule
import com.simple.doozy.feature.onboarding.di.onboardingModule
import com.simple.doozy.feature.todo.di.todoModule
import kotlinx.coroutines.CoroutineScope
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single
import org.koin.plugin.module.dsl.viewModel

val mainModule = module {
    single<CoroutineScope>{
        val application = androidContext().applicationContext as DoozyApplication
        application.applicationScope
    }
    single<DataStore<Preferences>> { androidContext().appDataStore }
    single<AuthManager>()
    viewModel<MainViewModel>()
    includes(todoModule)
    includes(authModule)
    includes(onboardingModule)

}