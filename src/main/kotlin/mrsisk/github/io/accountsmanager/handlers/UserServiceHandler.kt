package mrsisk.github.io.accountsmanager.handlers

import mrsisk.github.io.accountsmanager.service.UserService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.buildAndAwait
import kotlin.jvm.optionals.getOrNull

@Component
class UserServiceHandler(private val userService: UserService) {

    suspend fun getUserById(request: ServerRequest): ServerResponse{
        val id = request.pathVariable("id")
        val user = userService.findUserById(id) ?: return ServerResponse.notFound().buildAndAwait()
        return ServerResponse.ok().bodyValueAndAwait(user)
    }

    @OptIn(ExperimentalStdlibApi::class)
    suspend fun findAllUsers(request: ServerRequest): ServerResponse {
        val email = request.queryParam("email").getOrNull()

        if (email != null){
            val user = userService.findUserByEmail(email) ?: return ServerResponse.notFound().buildAndAwait()
            return ServerResponse.ok().bodyValueAndAwait(user)
        }

      val users = userService.findAllUsers()
      return ServerResponse.ok().bodyValueAndAwait(users)
    }
}