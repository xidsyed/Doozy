package com.simple.doozy.feature.todo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.simple.doozy.common.ui.CustomCheckbox
import com.simple.doozy.feature.todo.data.Todo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Todos(
    modifier: Modifier = Modifier,
    state: TodosState,
    setCompleted: (Todo, Boolean) -> Unit,
    removeTodo: (Todo) -> Unit,
    onTodoClick: (Todo) -> Unit
) {
    val dateFormatter = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())

    if (state.todos.isEmpty()) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "Press \"+\" to add a new todo.",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
        }
        return
    }

    LazyColumn(modifier = modifier.fillMaxWidth()) {
        items(state.todos, key = { it.id }) { todo ->
            val dismissState = rememberSwipeToDismissBoxState(
                positionalThreshold = { it * 0.8f }
            )

            androidx.compose.runtime.LaunchedEffect(dismissState.currentValue) {
                if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart || dismissState.currentValue == SwipeToDismissBoxValue.StartToEnd) {
                    removeTodo(todo)
                }
            }

            SwipeToDismissBox(
                modifier = Modifier.animateItem(),
                state = dismissState,
                enableDismissFromStartToEnd = true,
                enableDismissFromEndToStart = true,
                backgroundContent = {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.error)
                            .padding(horizontal = 24.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = "Delete Todo",
                            tint = MaterialTheme.colorScheme.onError
                        )
                    }
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .clickable(onClick = { onTodoClick(todo) })
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CustomCheckbox(
                            checked = todo.completed,
                            onCheckedChange = { checked -> setCompleted(todo, checked) }
                        )
                        Spacer(Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            val titleText = todo.title.ifBlank { "New Task" }
                            Text(
                                text = titleText,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Medium,
                                    textDecoration = if (todo.completed) TextDecoration.LineThrough else null,
                                    color = if (todo.completed) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Text(
                                text = dateFormatter.format(Date(todo.createdAt)),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                }
            }
        }
    }
}
