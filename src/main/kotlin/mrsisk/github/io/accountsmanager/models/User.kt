package mrsisk.github.io.accountsmanager.models

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

data class User(
    val id: String,
    val username: String,
    val createdTimestamp: Long,
    val enabled: Boolean,
    val totp: Boolean,
    val emailVerified: Boolean,
    val firstName: String?,
    val lastName: String?,
    val email: String,
    val requiredActions: List<String>,

)
data class UserInfo(val sub: String, val email: String)