package com.simple.doozy.feature.todo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simple.doozy.feature.todo.data.Todo
import com.simple.doozy.ui.theme.ScreenPaddingValues


@Composable
fun TodosScreen(
    modifier: Modifier,
    viewModel: TodosViewModel,
    navigateToTodoDetail: (Todo) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val setCompleted: (Todo, Boolean) -> Unit = remember(viewModel) {
        { todo, completed ->
            viewModel.onEvent(
                TodosEvent.UpdateTodo(
                    todo.copy(completed = completed)
                )
            )
        }
    }

    Column(
        modifier.padding(ScreenPaddingValues).fillMaxSize()
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(vertical = 8.dp, horizontal = 8.dp)
        ) {
            Text("Doozy", style = MaterialTheme.typography.titleLarge)
        }
        Spacer(Modifier.height(16.dp))
        Todos(
            modifier = Modifier.weight(1f),
            state = state,
            setCompleted = setCompleted,
            onTodoClick = navigateToTodoDetail
        )
    }
}
