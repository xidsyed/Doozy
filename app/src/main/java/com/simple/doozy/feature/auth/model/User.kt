package com.simple.doozy.feature.auth.model


data class User(
    val id: Id,
    val metadata: Metadata
) {
    data class Id(
        val id: String,
        val name: String,
        val email: String,
        val avatar: String
    )

    data class Metadata(
        val subscribeToEmails: Boolean,
        val isCatPerson: Boolean? = null
    )


    companion object {
        val MOCK = User(
            Id(
                id = "1",
                name = "John Doe",
                avatar = "https://picsum.photos/id/64/300/300",
                email = "janedoe@gmail.com",
            ),
            Metadata(
                subscribeToEmails = true,
                isCatPerson = true
            )
        )
    }
}