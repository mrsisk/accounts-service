package mrsisk.github.io.accountsmanager.models

data class RegistrationBody(
    val email: String,
    val username: String,
    val firstName: String,
    val lastName: String,
    val enabled: Boolean = true,
    val credentials: List<Credentials>,
    val emailVerified: Boolean = false,
    val requiredActions: List<String> = emptyList()
)

data class Credentials(val type: String = "password", val value: String, val temporary: Boolean)