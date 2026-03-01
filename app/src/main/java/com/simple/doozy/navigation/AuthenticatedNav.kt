package com.simple.doozy.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

enum class TopLevelDestination { Todos, Profile }

@Composable
fun AuthenticatedNav(modifier: Modifier) {
    var activeTab by remember { mutableStateOf(TopLevelDestination.Todos) }

    val todosBackStack = rememberNavBackStack(Route.Authenticated.Todos.TodosList)
    val profileBackStack = rememberNavBackStack(Route.Authenticated.Profile)

    val activeBackStack = when (activeTab) {
        TopLevelDestination.Todos -> todosBackStack
        TopLevelDestination.Profile -> profileBackStack
    }

    val currentRoute = activeBackStack.lastOrNull()
    val showBottomBar =
        currentRoute is Route.Authenticated.Todos.TodosList || currentRoute is Route.Authenticated.Profile

    Scaffold(
        modifier = modifier,
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter =  expandVertically() + slideInVertically { it } ,
                exit = shrinkVertically () + slideOutVertically { it }
            ) {
                NavigationBar {
                    NavigationBarItem(
                        selected = activeTab == TopLevelDestination.Todos,
                        onClick = { activeTab = TopLevelDestination.Todos },
                        icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Todos") },
                        label = { Text("Todos") }
                    )
                    NavigationBarItem(
                        selected = activeTab == TopLevelDestination.Profile,
                        onClick = { activeTab = TopLevelDestination.Profile },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                        label = { Text("Profile") }
                    )
                }

            }
        }
    ) { innerPadding ->
        NavDisplay(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            backStack = activeBackStack,
            onBack = { activeBackStack.removeLastOrNull() },
            entryProvider = entryProvider {
                entry<Route.Authenticated.Todos.TodosList> {
                    val todosViewModel = koinViewModel<TodosViewModel>()
                    TodosScreen(
                        modifier = Modifier,
                        viewModel = todosViewModel,
                        navigateToTodoDetail = { id -> activeBackStack.add(Route.Authenticated.Todos.TodoDetail(id)) }
                    )
                }
                entry<Route.Authenticated.Todos.TodoDetail> { route ->
                    val todoDetailViewModel = koinViewModel<TodoDetailViewModel> {
                        parametersOf(route.todoId ?: "")
                    }
                    TodoDetailsScreen(
                        modifier = Modifier,
                        viewModel = todoDetailViewModel,
                        navigateUp = { activeBackStack.removeLastOrNull() }
                    )
                }
                entry<Route.Authenticated.Profile> {
                    Text("Profile Screen Placeholder")
                }
            }
        )
    }
}