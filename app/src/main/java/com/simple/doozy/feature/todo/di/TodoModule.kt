package com.simple.doozy.feature.todo.di

import com.simple.doozy.feature.todo.TodosViewModel
import com.simple.doozy.feature.todo.data.DebouncedUpdateTodoUseCase
import com.simple.doozy.feature.todo.data.TodoRepository
import com.simple.doozy.feature.todo.detail.TodoDetailViewModel
import org.koin.dsl.module
import org.koin.plugin.module.dsl.*


val todoModule = module {
    single<TodoRepository>()
    single<DebouncedUpdateTodoUseCase>()
    viewModel<TodosViewModel>()
    viewModel<TodoDetailViewModel>()
}