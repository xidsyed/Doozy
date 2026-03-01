package com.simple.doozy.feature.todo.detail

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simple.doozy.feature.todo.data.SubTask
import com.simple.doozy.feature.todo.data.Todo
import com.simple.doozy.feature.todo.data.TodoRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface TodoDetailScreenState {
    data class Error(val message: String) : TodoDetailScreenState
    data class Success(
        val id: String,
        val title: TextFieldValue,
        val notes: TextFieldValue,
        val subtasks: List<SubTask>,
        val createdAt: Long,
        val completed: Boolean
    ) : TodoDetailScreenState
}

class TodoDetailViewModel(
    initialTodoId: String,
    private val todoRepository: TodoRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<TodoDetailScreenState>(
        if (initialTodoId.isBlank()) {
            TodoDetailScreenState.Success(
                id = java.util.UUID.randomUUID().toString(),
                title = TextFieldValue(""),
                notes = TextFieldValue(""),
                subtasks = emptyList(),
                createdAt = System.currentTimeMillis(),
                completed = false
            )
        } else {
            todoRepository.getOrNull(initialTodoId)?.let { initialTodo ->
                TodoDetailScreenState.Success(
                    id = initialTodo.id,
                    title = TextFieldValue(initialTodo.title),
                    notes = TextFieldValue(initialTodo.notes),
                    subtasks = initialTodo.subtasks,
                    createdAt = initialTodo.createdAt,
                    completed = initialTodo.completed
                )
            } ?: TodoDetailScreenState.Error("Todo not found")
        }
    )

    val state = _state.asStateFlow()

    var debouncedJob: Job? = null

    fun updateTodo(
        title: TextFieldValue? = null,
        notes: TextFieldValue? = null,
        subtasks: List<SubTask>? = null,
        completed: Boolean? = null
    ) {
        _state.update {
            val state = it as? TodoDetailScreenState.Success ?: return
            debouncedJob?.cancel()
            debouncedJob = viewModelScope.launch {
                delay(300)
                todoRepository.updateTodo(
                    Todo(
                        id = state.id,
                        title = title?.text ?: state.title.text,
                        notes = notes?.text ?: state.notes.text,
                        subtasks = subtasks ?: state.subtasks,
                        createdAt = state.createdAt,
                        completed = completed ?: state.completed
                    )
                )
            }
            state.copy(
                title = title ?: state.title,
                notes = notes ?: state.notes,
                subtasks = subtasks ?: state.subtasks,
                completed = completed ?: state.completed
            )
        }
    }

    fun deleteTodo() {
        val state = _state.value as? TodoDetailScreenState.Success ?: return
        debouncedJob?.cancel()
        debouncedJob = viewModelScope.launch {
            todoRepository.removeTodo(
                Todo(
                    id = state.id,
                    title = state.title.text,
                    notes = state.notes.text,
                    subtasks = state.subtasks,
                    createdAt = state.createdAt,
                    completed = state.completed
                )
            )
        }
    }
}