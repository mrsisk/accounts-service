package mrsisk.github.io.accountsmanager.models

import com.fasterxml.jackson.annotation.JsonProperty

data class LoginRequest(val email: String, val password: String)

