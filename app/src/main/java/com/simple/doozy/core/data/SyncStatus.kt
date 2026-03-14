package com.simple.doozy.core.data

sealed interface SyncStatus {
    data class Idle(val lastSync: Long) : SyncStatus
    data object Loading : SyncStatus
    data class Error(val message: String) : SyncStatus
}
