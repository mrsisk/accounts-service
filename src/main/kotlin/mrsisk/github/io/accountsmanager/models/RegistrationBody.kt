package mrsisk.github.io.accountsmanager.models

data class RegistrationBody(
    val email: String,
    val username: String,
    val firstName: String,
    val lastName: String,
    val enabled: Boolean,
    val credentials: List<Credentials>
)

data class Credentials(val type: String = "password", val value: String, val temporary: Boolean)