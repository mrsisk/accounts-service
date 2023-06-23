package mrsisk.github.io.accountsmanager.service

import mrsisk.github.io.accountsmanager.models.User
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.*
import org.springframework.web.reactive.function.server.*


@Service
class UserService(private val client: WebClient) {

    @Value("\${auth-server.admin-uri}")
    lateinit var adminUrl: String

    suspend fun findAllUsers(request: ServerRequest): ServerResponse{
        try {

          val users = client.get()
              .uri("${adminUrl}/users")
              .awaitExchange {
                  if (it.statusCode().is2xxSuccessful) return@awaitExchange it.awaitBody(Array<User>::class)
                  else throw Exception("Error while fetching users")
              }
            return ServerResponse.ok().bodyValueAndAwait(users)
        }catch (err: Exception){
            return errorResponse(HttpStatus.NOT_FOUND, err.localizedMessage)
        }
    }

    suspend fun findUser(request: ServerRequest): ServerResponse{
        try {
            val id = request.pathVariable("id")
            val user = client.get()
                .uri("${adminUrl}/users/$id")
                .awaitExchange {
                    if (it.statusCode().is2xxSuccessful) return@awaitExchange it.awaitBody(User::class)
                    else throw Exception("user $id Not found")
                }
            return ServerResponse.ok().json().bodyValueAndAwait(user)
        }catch (ex: Exception){
            return errorResponse(HttpStatus.NOT_FOUND, ex.localizedMessage)
        }


    }

    private suspend fun errorResponse(code: HttpStatus, message: String): ServerResponse{
        return ServerResponse.status(code).json().bodyValueAndAwait(mapOf("error" to message))
    }
}