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
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

@Component
class AuthenticationServiceHandler(private val authService: AuthService) {

    suspend fun login(serverRequest: ServerRequest): ServerResponse{
        val body = serverRequest.bodyToMono<LoginRequest>().awaitFirst()
        val response = authService.authenticate(body.email, body.password)
        return handleAuthResponse(response)
    }

    suspend fun signOut(serverRequest: ServerRequest): ServerResponse{
        val cookie = ResponseCookie.from("refresh_token").build()
        return ServerResponse
            .status(HttpStatus.OK).header(HttpHeaders.SET_COOKIE, cookie.toString())
            .buildAndAwait()
    }

    suspend fun refresh(serverRequest: ServerRequest): ServerResponse {

        println("COOKIES ARE ${serverRequest.cookies()}")
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
            is Result.Error -> {
                println("AUTH ERROR $result")
                ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValueAndAwait(result.error)
            }
            is Result.Success -> {
                val cookie = ResponseCookie.from("refresh_token", result.data.refreshToken)
                    .httpOnly(false)
                 //   .path("/")
                    .secure(false)
                    .maxAge(30 * 60 * 1000)
                    .build()
                ServerResponse
                    .status(HttpStatus.OK).header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .bodyValueAndAwait(mapOf("access_token" to result.data.accessToken))
            }
        }
    }

    suspend fun userInfo(serverRequest: ServerRequest): ServerResponse{
        println("HANDLING 1")
        val s = serverRequest.principal().awaitFirst() as JwtAuthenticationToken
        print("TOKEN IS $s")
        return ServerResponse.ok().bodyValueAndAwait(s.tokenAttributes)
    }
}