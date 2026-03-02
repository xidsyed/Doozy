package com.simple.doozy.feature.auth.model


data class User(
    val id: Id,
    val metadata: Metadata? = null
) {
    data class Id(
        val id: String
    )

    data class Metadata(
        val name: String? = null,
        val gender: String? = null,
        val email: String? = null,
        val subscribeToEmails: Boolean?
    )


    companion object {
        val MOCK = User(
            Id(
                id = "1",
            ),
            null
        )
    }
}