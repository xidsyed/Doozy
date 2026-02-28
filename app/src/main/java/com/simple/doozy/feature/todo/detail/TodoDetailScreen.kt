package com.simple.doozy.feature.todo.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simple.doozy.ui.theme.ScreenPaddingValues
import com.simple.doozy.common.ui.util.AppPreview

@Composable
fun TodoDetailsScreen(modifier: Modifier, viewModel: TodoDetailViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val updateTodo = viewModel::updateTodo
    Surface {
        TodoDetails(
            modifier = modifier.fillMaxSize().padding(ScreenPaddingValues), state = state, updateTodo = updateTodo
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TodoDetails(
    modifier: Modifier = Modifier,
    state: TodoDetailScreenState,
    updateTodo: (title: TextFieldValue?, notes: TextFieldValue?, completed: Boolean?) -> Unit
) {
    if (state !is TodoDetailScreenState.Success) {
        val errState = state as TodoDetailScreenState.Error
        Box(contentAlignment = Alignment.Center) {
            Text(text = errState.message)
        }
        return
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = modifier.fillMaxWidth()) {
        val focusManager = LocalFocusManager.current
        val isKeyboardVisible = WindowInsets.isImeVisible

        // When keyboard closes → clear focus from ALL text fields
        LaunchedEffect(isKeyboardVisible) {
            if (!isKeyboardVisible) {
                focusManager.clearFocus()
            }
        }

        BasicTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.title,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            onValueChange = {
                updateTodo(
                    it.removeNextLine(), null, null
                )
            },
            textStyle = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            ),
            decorationBox = { innerTextField ->
                Box {
                    // Placeholder
                    if (state.title.text.isEmpty()) {
                        Text(
                            text = "Title",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                            )
                        )
                    }

                    // Actual text field
                    innerTextField()
                }
            },
            maxLines = 3,
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Notes") },
            value = state.notes,
            minLines = 3,
            onValueChange = {
                updateTodo(
                    null, it.removeNextLine(), null
                )
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Completed", color = MaterialTheme.colorScheme.secondary)
            Checkbox(
                checked = state.completed, onCheckedChange = {
                    updateTodo(
                        null, null, it
                    )
                })
        }
    }
}

private fun TextFieldValue.removeNextLine(): TextFieldValue = copy(text = text.replace("\n", ""))


@PreviewLightDark
@Composable
private fun TodoDetailsPreview() {
    AppPreview {
        var state by remember {
            mutableStateOf(
                TodoDetailScreenState.Success(
                    id = "1",
                    title = TextFieldValue("Buy groceries"),
                    notes = TextFieldValue("Apples, Milk, Bread"),
                    completed = false
                )
            )
        }
        TodoDetails(
            modifier = Modifier.padding(ScreenPaddingValues),
            state = state,
            updateTodo = { title, notes, completed ->
                state = state.copy(
                    title = title ?: state.title,
                    notes = notes ?: state.notes,
                    completed = completed ?: state.completed
                )
            })
    }
}
