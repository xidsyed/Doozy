package com.simple.doozy.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.simple.doozy.feature.todo.TodosScreen
import com.simple.doozy.feature.todo.TodosViewModel
import com.simple.doozy.feature.todo.detail.TodoDetailViewModel
import com.simple.doozy.feature.todo.detail.TodoDetailsScreen
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AuthenticatedNav(modifier: Modifier) {

    val backstack = rememberNavBackStack(Route.Authenticated.Todos)

    NavDisplay(
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        backStack = backstack,
        onBack = { backstack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<Route.Authenticated.Todos> {
                val todosViewModel = koinViewModel<TodosViewModel>()
                TodosScreen(
                    modifier = modifier,
                    viewModel = todosViewModel,
                    navigateToTodoDetail = { backstack.add(Route.Authenticated.TodoDetail(it.id)) }
                )
            }
            entry<Route.Authenticated.TodoDetail> { route ->
                val todoDetailViewModel = koinViewModel<TodoDetailViewModel> {
                    parametersOf(route.todoId)
                }
                TodoDetailsScreen(modifier, todoDetailViewModel)
            }
        }
    )
}