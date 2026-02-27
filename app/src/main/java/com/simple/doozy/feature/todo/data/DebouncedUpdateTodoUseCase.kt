package com.simple.doozy.feature.todo.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * For Performing actions scoped to the application's scope preventing lost-updates
 * */
class DebouncedUpdateTodoUseCase(
    private val todoRepository: TodoRepository,
    private val appScope: CoroutineScope
) {

    private var updateJobs = mutableMapOf<String, Job>()

    operator fun invoke(todo: Todo) {
        updateJobs[todo.id]?.cancel()
        updateJobs[todo.id] = appScope.launch(Dispatchers.IO) {
            delay(500)
            todoRepository.updateTodo(todo)
        }
    }

}

