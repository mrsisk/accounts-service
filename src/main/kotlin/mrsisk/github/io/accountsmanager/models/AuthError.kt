package mrsisk.github.io.accountsmanager.models

import com.fasterxml.jackson.annotation.JsonProperty

data class AuthError(val error: String?, @JsonProperty("error_description") val errorDescription: String?)
