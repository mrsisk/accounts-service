package mrsisk.github.io.accountsmanager.models

data class RegistrationRequest(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String
)
