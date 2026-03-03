package com.simple.doozy.feature.todo.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simple.doozy.common.ui.CustomCheckbox
import com.simple.doozy.common.ui.util.AppPreview
import com.simple.doozy.feature.todo.data.SubTask
import com.simple.doozy.ui.theme.DefaultRoundedShape
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun TodoDetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: TodoDetailViewModel,
    navigateUp: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val updateTodo = viewModel::updateTodo

    Surface(color = MaterialTheme.colorScheme.background) {
        TodoDetails(
            modifier = modifier.fillMaxSize(),
            state = state,
            updateTodo = updateTodo,
            onDelete = {
                viewModel.deleteTodo()
                navigateUp()
            }
        )
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun TodoDetails(
    modifier: Modifier = Modifier,
    state: TodoDetailScreenState,
    updateTodo: (title: TextFieldValue?, notes: TextFieldValue?, subtasks: List<SubTask>?, completed: Boolean?) -> Unit,
    onDelete: () -> Unit
) {
    if (state !is TodoDetailScreenState.Success) {
        val errState = state as? TodoDetailScreenState.Error
        Box(contentAlignment = Alignment.Center, modifier = modifier) {
            Text(text = errState?.message ?: "Loading...")
        }
        return
    }

    val focusManager = LocalFocusManager.current
    val isKeyboardVisible = WindowInsets.isImeVisible

    LaunchedEffect(isKeyboardVisible) {
        if (!isKeyboardVisible) {
            focusManager.clearFocus()
        }
    }

    val dateFormatter = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Scrollable content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(top = 16.dp)
        ) {
            // Header Row: Checkbox + Title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(modifier = Modifier.padding(top = 6.dp)) {
                    CustomCheckbox(
                        checked = state.completed,
                        onCheckedChange = { updateTodo(null, null, null, it) }
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                BasicTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.title,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    onValueChange = {
                        val newTitle = it.copy(text = it.text.replace("\n", ""))
                        updateTodo(newTitle, null, null, null)
                    },
                    textStyle = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    decorationBox = { innerTextField ->
                        Box {
                            if (state.title.text.isEmpty()) {
                                Text(
                                    text = "Task Title",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                                    )
                                )
                            }
                            innerTextField()
                        }
                    },
                    maxLines = 3,
                )
            }

            // Subtasks Section
            Column(modifier = Modifier.padding(horizontal = 24.dp).padding(top = 16.dp)) {
                Text(
                    text = "SUB-TASKS",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Drag and Drop Subtasks
                var draggedItemIndex by remember { mutableStateOf<Int?>(null) }
                var dragOffset by remember { mutableFloatStateOf(0f) }
                val density = LocalDensity.current
                val itemHeightPx = with(density) { 56.dp.toPx() }

                Column(modifier = Modifier.fillMaxWidth()) {
                    state.subtasks.forEachIndexed { index, subtask ->
                        val isDragged = draggedItemIndex == index
                        val zIndex = if (isDragged) 1f else 0f
                        val yOffset = if (isDragged) dragOffset.roundToInt() else 0

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .zIndex(zIndex)
                                .offset { IntOffset(0, yOffset) }
                                .background(if (isDragged) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent)
                                .padding(vertical = 4.dp)
                                .pointerInput(Unit) {
                                    detectVerticalDragGestures(
                                        onDragStart = { draggedItemIndex = index },
                                        onDragEnd = {
                                            draggedItemIndex = null
                                            dragOffset = 0f
                                        },
                                        onDragCancel = {
                                            draggedItemIndex = null
                                            dragOffset = 0f
                                        },
                                        onVerticalDrag = { change, dragAmount ->
                                            change.consume()
                                            dragOffset += dragAmount

                                            if (dragOffset > itemHeightPx && draggedItemIndex!! < state.subtasks.size - 1) {
                                                val updated = state.subtasks.toMutableList()
                                                val currentIdx = draggedItemIndex!!
                                                val nextIdx = currentIdx + 1
                                                updated[currentIdx] =
                                                    updated[nextIdx].also { updated[nextIdx] = updated[currentIdx] }
                                                updateTodo(null, null, updated, null)
                                                draggedItemIndex = nextIdx
                                                dragOffset -= itemHeightPx
                                            } else if (dragOffset < -itemHeightPx && draggedItemIndex!! > 0) {
                                                val updated = state.subtasks.toMutableList()
                                                val currentIdx = draggedItemIndex!!
                                                val prevIdx = currentIdx - 1
                                                updated[currentIdx] =
                                                    updated[prevIdx].also { updated[prevIdx] = updated[currentIdx] }
                                                updateTodo(null, null, updated, null)
                                                draggedItemIndex = prevIdx
                                                dragOffset += itemHeightPx
                                            }
                                        }
                                    )
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CustomCheckbox(
                                checked = subtask.completed,
                                onCheckedChange = { checked ->
                                    val updatedSubtasks = state.subtasks.toMutableList()
                                    updatedSubtasks[index] = subtask.copy(completed = checked)
                                    updateTodo(null, null, updatedSubtasks, null)
                                }
                            )
                            Spacer(Modifier.width(12.dp))
                            BasicTextField(
                                value = subtask.title,
                                onValueChange = { newValue ->
                                    val updatedSubtasks = state.subtasks.toMutableList()
                                    updatedSubtasks[index] = subtask.copy(title = newValue.replace("\n", ""))
                                    updateTodo(null, null, updatedSubtasks, null)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 12.dp),
                                textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onBackground),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                decorationBox = { innerTextField ->
                                    Box {
                                        if (subtask.title.isEmpty()) {
                                            Text(
                                                "Empty subtask",
                                                style = MaterialTheme.typography.bodyLarge.copy(
                                                    color = MaterialTheme.colorScheme.onBackground.copy(
                                                        alpha = 0.4f
                                                    )
                                                )
                                            )
                                        }
                                        innerTextField()
                                    }
                                }
                            )
                            IconButton(
                                onClick = {
                                    val updatedSubtasks = state.subtasks.toMutableList()
                                    updatedSubtasks.removeAt(index)
                                    updateTodo(null, null, updatedSubtasks, null)
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = "Remove Subtask",
                                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                // Add Subtask Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val updatedSubtasks =
                                state.subtasks + SubTask(java.util.UUID.randomUUID().toString(), "", false)
                            updateTodo(null, null, updatedSubtasks, null)
                        }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Add sub-task",
                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = "Add sub-task",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
            )

            // Notes Section
            Column(modifier = Modifier.padding(horizontal = 24.dp).padding(top = 8.dp, bottom = 24.dp)) {
                Text(
                    text = "NOTES",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                BasicTextField(
                    modifier = Modifier.fillMaxWidth().height(160.dp),
                    value = state.notes,
                    onValueChange = { updateTodo(null, it, null, null) },
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(DefaultRoundedShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .padding(16.dp)
                        ) {
                            if (state.notes.text.isEmpty()) {
                                Text(
                                    "Add details regarding this task...",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            }
        }

        // Bottom Bar (Created At & Delete)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "CREATED AT",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                )
                Text(
                    text = dateFormatter.format(Date(state.createdAt)),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Row(
                modifier = Modifier
                    .clip(DefaultRoundedShape)
                    .clickable { onDelete() }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Delete,
                    contentDescription = "Delete task",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Delete task",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun TodoDetailsPreview() {
    AppPreview {
        var state by remember {
            mutableStateOf(
                TodoDetailScreenState.Success(
                    id = "1",
                    title = TextFieldValue("Design new iconography set"),
                    notes = TextFieldValue("Make sure to check the latest guidelines."),
                    subtasks = listOf(
                        SubTask("1", "Sketch initial concepts", false),
                        SubTask("2", "Review with design team", true)
                    ),
                    createdAt = System.currentTimeMillis(),
                    completed = false
                )
            )
        }
        TodoDetails(
            modifier = Modifier,
            state = state,
            updateTodo = { title, notes, subtasks, completed ->
                state = state.copy(
                    title = title ?: state.title,
                    notes = notes ?: state.notes,
                    subtasks = subtasks ?: state.subtasks,
                    completed = completed ?: state.completed
                )
            },
            onDelete = {}
        )
    }
}

