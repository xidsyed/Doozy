package com.simple.doozy.feature.todo.data

data class SubTask(
    val id: String,
    val title: String,
    val completed: Boolean = false
)

data class Todo(
    val id: String,
    val title: String,
    val notes: String,
    val subtasks: List<SubTask> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val completed: Boolean = false
)