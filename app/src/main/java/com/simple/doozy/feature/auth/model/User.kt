package com.simple.doozy.feature.auth.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val name: String? = null,
    val gender: String? = null,
    val email: String? = null,
    val subscribeToEmails: Boolean? = null
) {
    companion object {
        val MOCK = User(
            id = "1"
        )
    }
}