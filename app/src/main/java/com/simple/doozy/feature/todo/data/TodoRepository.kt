package com.simple.doozy.feature.todo.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TodoRepository {
    private var _todos = MutableStateFlow(
        listOf(
            Todo("1", "Buy milk", "", false),
            Todo("2", "Walk the dog", "", true),
            Todo("3", "Do the laundry", "", false)
        )

    );

    val todos: Flow<List<Todo>>
        get() = _todos.asStateFlow()

    fun getOrNull(id: String): Todo? {
        return _todos.value.find { it.id == id }
    }

    fun addTodo(todo: Todo) {
        _todos.update { it + todo }
    }

    fun removeTodo(todo: Todo) {
        _todos.update { it - todo }
    }

    fun updateTodo(updateTodo: Todo) {
        _todos.update { todos -> todos.map { todo -> if (todo.id == updateTodo.id) updateTodo else todo } }
    }
}