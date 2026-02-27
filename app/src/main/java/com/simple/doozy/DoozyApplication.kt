package com.simple.doozy

import android.app.Application
import com.simple.doozy.di.mainModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.logger.Level

class DoozyApplication : Application() {

    val applicationScope =  CoroutineScope(
        SupervisorJob() + Dispatchers.Default
    )

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@DoozyApplication)
            androidLogger(level= Level.INFO)
            modules(mainModule)
        }
    }

}