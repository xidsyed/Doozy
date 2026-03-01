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
        val gender: String? = null
    )


    companion object {
        val MOCK = User(
            Id(
                id = "1",
                name = "Alex Doe",
                avatar = "https://lh3.googleusercontent.com/aida-public/AB6AXuB_W8KmEaJn-Z7AU6vzDA3KnlsZhDTlI2FRdYzu4MYCG87apARoxc5tQQ8FpNUWrIsy0M3_G_oBMJaxIhdfV7hto5I5iSYifzgCCd_r-bhd0lAOvFsLtf54goTWjvgAbNhJgpcFfW7gFSIg5eWRfOTdaRzJCd1ZwEd0BY_hK2qgfcpmxzU6FIRJdrqFWcIcWjlUfNaWmaQxhM_JHlm5UUMPVM6cXvKXAFNgwG_jWfvTVy4Ipnh_caEyfGZKwztUZkAdKMWbf1UiGMg",
                email = "alex.doe@example.com",
            ),
            Metadata(
                subscribeToEmails = true,
                gender = "Male"
            )
        )
    }
}