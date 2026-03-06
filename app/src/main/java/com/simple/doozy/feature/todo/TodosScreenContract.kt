package com.simple.doozy.feature.todo

import com.simple.doozy.feature.todo.data.Todo


sealed interface TodosEvent {
    data class AddTodo(val todo: Todo) : TodosEvent
    data class RemoveTodo(val todo: Todo) : TodosEvent
    data class UpdateTodo(val updateTodo: Todo) : TodosEvent
    data object OnAddTodoClick : TodosEvent
}

sealed interface TodosUiEvent {
    data object ShowPaywall : TodosUiEvent
    data object NavigateToDetail : TodosUiEvent
}

data class TodosState(val todos: List<Todo>)