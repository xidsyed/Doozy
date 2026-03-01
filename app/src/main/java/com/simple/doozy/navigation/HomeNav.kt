package com.simple.doozy.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import com.simple.doozy.feature.profile.EditProfileScreen
import com.simple.doozy.feature.profile.EditProfileViewModel
import com.simple.doozy.feature.profile.ProfileScreen
import com.simple.doozy.feature.profile.ProfileViewModel
import com.simple.doozy.feature.todo.TodosListScreen
import com.simple.doozy.feature.todo.TodosListViewModel
import com.simple.doozy.feature.todo.detail.TodoDetailsScreen
import com.simple.doozy.navigation.route.Route.AuthenticatedNav.HomeNav
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf


@Composable
fun HomeNav(modifier: Modifier) {

    var showBottomBar by remember { mutableStateOf(true) }
    var activeTab: HomeNav.BottomNavTab by remember { mutableStateOf(HomeNav.TodosTab) }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = expandVertically() + slideInVertically { it },
                exit = shrinkVertically() + slideOutVertically { it }
            ) {
                NavigationBar {
                    NavigationBarItem(
                        selected = activeTab == HomeNav.TodosTab,
                        onClick = { activeTab = HomeNav.TodosTab },
                        icon = {
                            Icon(
                                Icons.AutoMirrored.Filled.List,
                                contentDescription = "Todos"
                            )
                        },
                        label = { Text("Todos") }
                    )
                    NavigationBarItem(
                        selected = activeTab == HomeNav.ProfileTab,
                        onClick = { activeTab = HomeNav.ProfileTab },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                        label = { Text("Profile") }
                    )
                }

            }
        }
    ) { innerPadding ->

        val tabDestinationModifier = modifier.padding(innerPadding)

        if (activeTab == HomeNav.TodosTab) {
            TodoNav(tabDestinationModifier, showBottomBar = { showBottomBar = it })
        } else {
            SettingsNav(tabDestinationModifier, showBottomBar = { showBottomBar = it })
        }
    }
}

@Composable
fun SettingsNav(modifier: Modifier, showBottomBar: (Boolean) -> Unit) {
    val backStack = rememberNavBackStack(HomeNav.ProfileTab.Profile)

    if(backStack.size > 1 ){
        showBottomBar(false)
    } else {
        showBottomBar(true)
    }

    NavDisplay(
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<HomeNav.ProfileTab.Profile> {
                val profileViewModel = koinViewModel<ProfileViewModel>()
                ProfileScreen(
                    modifier = modifier,
                    viewModel = profileViewModel,
                    onNavigateToEditProfile = { backStack.add(HomeNav.ProfileTab.EditProfile) }
                )
            }
            entry<HomeNav.ProfileTab.EditProfile> {
                val editProfileViewModel = koinViewModel<EditProfileViewModel>()
                EditProfileScreen(
                    modifier = modifier,
                    viewModel = editProfileViewModel,
                    onNavigateBack = { backStack.removeLastOrNull() }
                )
            }
        }
    )
}

@Composable
fun TodoNav(modifier: Modifier, showBottomBar: (Boolean) -> Unit) {
    val backStack = rememberNavBackStack(HomeNav.TodosTab.TodosList)

    if(backStack.size > 1 ){
        showBottomBar(false)
    } else {
        showBottomBar(true)
    }

    NavDisplay(
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<HomeNav.TodosTab.TodosList> {
                val todosListViewModel = koinViewModel<TodosListViewModel>()
                TodosListScreen(
                    modifier = modifier,
                    viewModel = todosListViewModel,
                    navigateToTodoDetail = { id ->
                        backStack.add(
                            HomeNav.TodosTab.TodoDetail(
                                id
                            )
                        )
                    }
                )
            }
            entry<HomeNav.TodosTab.TodoDetail> { route ->
                val todoDetailViewModel =
                    koinViewModel<com.simple.doozy.feature.todo.detail.TodoDetailViewModel> {
                        parametersOf(route.todoId ?: "")
                    }
                TodoDetailsScreen(
                    modifier = modifier,
                    viewModel = todoDetailViewModel,
                    navigateUp = { backStack.removeLastOrNull() }
                )
            }

        }
    )
}

