package com.simple.doozy.feature.todo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simple.doozy.feature.todo.data.TodoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TodosViewModel(
    private val todoRepo: TodoRepository,
) : ViewModel() {

    val state = todoRepo.todos.map {
        TodosState(it)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(3000), TodosState(emptyList()))

    fun onEvent(todoEvent: TodosEvent) {
        viewModelScope.launch {
            when (todoEvent) {
                is TodosEvent.AddTodo -> todoRepo.addTodo(todoEvent.todo)
                is TodosEvent.RemoveTodo -> todoRepo.removeTodo(todoEvent.todo)
                is TodosEvent.UpdateTodo -> todoRepo.updateTodo(todoEvent.updateTodo)
            }
        }
    }

}