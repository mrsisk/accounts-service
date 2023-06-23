package mrsisk.github.io.accountsmanager.handlers

import kotlinx.coroutines.reactive.awaitFirst
import mrsisk.github.io.accountsmanager.models.AuthError
import mrsisk.github.io.accountsmanager.models.AuthenticationResponse
import mrsisk.github.io.accountsmanager.models.LoginRequest
import mrsisk.github.io.accountsmanager.models.Result
import mrsisk.github.io.accountsmanager.service.AuthService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

@Component
class AuthenticationHandler(private val authService: AuthService) {

    suspend fun login(serverRequest: ServerRequest): ServerResponse{
        val body = serverRequest.bodyToMono<LoginRequest>().awaitFirst()
        val response = authService.authenticate(body.email, body.password)
        return handleAuthResponse(response)
    }

    suspend fun refresh(serverRequest: ServerRequest): ServerResponse {
        val list = serverRequest.cookies()["refresh_token"]
            ?: return ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValueAndAwait(mutableMapOf("message" to "no fresh  token found"))

        if (list.isEmpty()) return ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValueAndAwait(mutableMapOf("message" to "no fresh  token found"))
        val tokenCookie = list.first()
        val token = tokenCookie.value
        val response = authService.refresh(token)
        return handleAuthResponse(response)
    }

    suspend fun handleAuthResponse(result: Result<AuthenticationResponse, AuthError>): ServerResponse{
        return when(result){
            is Result.Error -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValueAndAwait(result.error)
            is Result.Success -> {
                val cookie = ResponseCookie.from("refresh_token", result.data.refreshToken)
                    .httpOnly(true)
                    .maxAge(30 * 60 * 1000)
                    .build();
                ServerResponse
                    .status(HttpStatus.OK).header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .bodyValueAndAwait(mapOf("access_token" to result.data.accessToken))
            }
        }
    }
}