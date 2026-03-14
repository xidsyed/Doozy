package com.simple.doozy.feature.todo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simple.doozy.feature.subscription.data.SubscriptionRepository
import com.simple.doozy.feature.todo.data.TodoRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TodosListViewModel(
    private val todoRepo: TodoRepository,
    private val subscriptionRepository: SubscriptionRepository
) : ViewModel() {

    val state = todoRepo.todos.map {
        TodosState(it)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(3000), TodosState(emptyList()))

    val isSubscribed = subscriptionRepository.state
        .map { it.data is com.simple.doozy.feature.subscription.data.SubscriptionData.Active }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(3000), false)

    private val _uiEvent = MutableSharedFlow<TodosUiEvent>()
    val uiEvent: SharedFlow<TodosUiEvent> = _uiEvent

    fun onEvent(todoEvent: TodosEvent) {
        viewModelScope.launch {
            when (todoEvent) {
                is TodosEvent.AddTodo -> todoRepo.addTodo(todoEvent.todo)
                is TodosEvent.RemoveTodo -> todoRepo.removeTodo(todoEvent.todo)
                is TodosEvent.UpdateTodo -> todoRepo.updateTodo(todoEvent.updateTodo)
                is TodosEvent.OnAddTodoClick -> {
                    val currentTodosCount = state.value.todos.size
                    if (currentTodosCount >= 5 && !isSubscribed.value) {
                        _uiEvent.emit(TodosUiEvent.ShowPaywall)
                    } else {
                        _uiEvent.emit(TodosUiEvent.NavigateToDetail)
                    }
                }
            }
        }
    }

}