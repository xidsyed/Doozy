package com.simple.doozy.feature.todo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.simple.doozy.feature.todo.data.Todo
import com.simple.doozy.ui.theme.DefaultRoundedShape
import com.simple.doozy.common.ui.util.AppPreview

@Composable
fun Todos(
    modifier: Modifier,
    state: TodosState,
    setCompleted: (Todo, Boolean) -> Unit,
    onTodoClick: (Todo) -> Unit
) {
    LazyColumn(modifier = modifier.fillMaxWidth()) {
        items(state.todos, key = { it.id }) { todo ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(DefaultRoundedShape)
                    .clickable(onClick = { onTodoClick(todo) })
                    .padding(vertical = 8.dp, horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val onSurface = MaterialTheme.colorScheme.onSurface

                val titleAndColor by remember(todo.title, onSurface) {
                    derivedStateOf {
                        val isBlank = todo.title.isBlank()

                        Pair(
                            todo.title.ifBlank { "Untitled" },
                            onSurface.copy(alpha = if (isBlank) 0.4f else 1f)
                        )
                    }
                }

                Text(
                    modifier = Modifier.weight(1f),
                    text = titleAndColor.first,
                    maxLines = 2,
                    overflow = TextOverflow.StartEllipsis,
                    color = titleAndColor.second
                )
                Spacer(Modifier.width(4.dp))
                Checkbox(
                    checked = todo.completed,
                    onCheckedChange = { checked ->
                        setCompleted(todo, checked)
                    }
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
fun TodosAppPreview() {
    AppPreview {
        val todos = remember {
            mutableStateListOf<Todo>(
                Todo("1", "Buy milk", "", false),
                Todo("2", "Walk the dog", "", true),
                Todo("3", "Do the laundry", "", false)
            )
        }
        val state = TodosState(todos = todos)
        Todos(
            modifier = Modifier,
            state = state,
            setCompleted = { todo, value ->
                todos.map { if (it.id == todo.id) it.copy(completed = value) else it }
            },
            onTodoClick = {}
        )
    }
}
