package com.simple.doozy.feature.todo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simple.doozy.feature.todo.data.Todo
import com.simple.doozy.ui.theme.PremiumGold
import com.simple.doozy.ui.theme.ScreenPaddingValues

@Composable
fun TodosScreen(
    modifier: Modifier = Modifier,
    viewModel: TodosViewModel,
    navigateToTodoDetail: (String?) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    var activeFilter by remember { mutableStateOf("All") }
    val filteredTodos = remember(state.todos, activeFilter) {
        when (activeFilter) {
            "Pending" -> state.todos.filter { !it.completed }
            else -> state.todos
        }
    }

    val setCompleted: (Todo, Boolean) -> Unit = remember(viewModel) {
        { todo, completed ->
            viewModel.onEvent(TodosEvent.UpdateTodo(todo.copy(completed = completed)))
        }
    }

    val removeTodo: (Todo) -> Unit = remember(viewModel) {
        { todo ->
            viewModel.onEvent(TodosEvent.RemoveTodo(todo))
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize().padding(ScreenPaddingValues),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navigateToTodoDetail(null)
                },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.onSurface,
                contentColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.padding(bottom = 16.dp, end = 8.dp)
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "Add Todo", modifier = Modifier.size(32.dp))
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).fillMaxSize()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Doozy",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                // Premium Badge
                Surface(
                    shape = CircleShape,
                    color = PremiumGold,
                    contentColor = MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Star,
                            contentDescription = "Premium",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Get Premium",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            // Filters
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Column(
                    modifier = Modifier.clickable { activeFilter = "All" }.padding(horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "All",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (activeFilter == "All") MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Box(
                        modifier = Modifier
                            .height(2.dp)
                            .width(24.dp)
                            .background(if (activeFilter == "All") MaterialTheme.colorScheme.onSurface else Color.Transparent)
                    )
                }
                Spacer(modifier = Modifier.width(24.dp))
                Column(
                    modifier = Modifier.clickable { activeFilter = "Pending" }.padding(horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Pending",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium,
                            color = if (activeFilter == "Pending") MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Box(
                        modifier = Modifier
                            .height(2.dp)
                            .width(60.dp)
                            .background(if (activeFilter == "Pending") MaterialTheme.colorScheme.onSurface else Color.Transparent)
                    )
                }
            }

            Todos(
                modifier = Modifier.weight(1f),
                state = state.copy(todos = filteredTodos),
                setCompleted = setCompleted,
                removeTodo = removeTodo,
                onTodoClick = { navigateToTodoDetail(it.id) }
            )
        }
    }
}
