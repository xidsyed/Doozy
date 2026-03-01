package com.simple.doozy.feature.todo.di

import com.simple.doozy.feature.todo.TodosViewModel
import com.simple.doozy.feature.todo.data.TodoRepository
import com.simple.doozy.feature.todo.detail.TodoDetailViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val TodoModule = module {
    singleOf(::TodoRepository)
    viewModelOf(::TodosViewModel)
    viewModelOf(::TodoDetailViewModel)
}