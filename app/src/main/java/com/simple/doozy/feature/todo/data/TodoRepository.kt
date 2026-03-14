package com.simple.doozy.feature.todo.data

import com.simple.doozy.feature.session.UserSessionClearable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TodoRepository : UserSessionClearable {
    private var _todos = MutableStateFlow(
        listOf(
            Todo("1", "Buy milk", "2% milk", emptyList(), System.currentTimeMillis(), false),
            Todo(
                "2",
                "Walk the dog",
                "Around the park",
                listOf(
                    SubTask("s1", "Get leash", false),
                    SubTask("s2", "Get bags", true)
                ),
                System.currentTimeMillis(),
                true
            ),
            Todo("3", "Do the laundry", "Whites and colors", emptyList(), System.currentTimeMillis(), false)
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
        _todos.update { todos ->
            if (todos.any { it.id == updateTodo.id }) {
                todos.map { todo -> if (todo.id == updateTodo.id) updateTodo else todo }
            } else {
                todos + updateTodo
            }
        }
    }

    override suspend fun clearSessionData() {
        _todos.update { emptyList() }
    }
}