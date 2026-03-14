package com.simple.doozy.feature.user.data

import com.simple.doozy.core.data.SyncStatus
import com.simple.doozy.feature.auth.model.User

data class UserRepositoryState(
    val syncStatus: SyncStatus = SyncStatus.Idle(0L),
    val data: User? = null
)
