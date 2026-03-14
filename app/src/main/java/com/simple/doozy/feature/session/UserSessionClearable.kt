package com.simple.doozy.feature.session

interface UserSessionClearable {
    suspend fun clearSessionData()
}
