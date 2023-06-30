package mrsisk.github.io.accountsmanager.service

import mrsisk.github.io.accountsmanager.models.User
import mrsisk.github.io.accountsmanager.models.UserInfo
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.*
import org.springframework.web.reactive.function.server.*
import javax.naming.AuthenticationException


@Service
class UserService(private val client: WebClient) {

    @Value("\${auth-server.admin-uri}")
    lateinit var adminUrl: String

    companion object{
        private val logger = LoggerFactory.getLogger(UserService::class.java)
    }
    suspend fun findAllUsers(): Array<User>{

          return client.get()
              .uri("${adminUrl}/users")
              .awaitExchange {
                  if (it.statusCode().is2xxSuccessful) return@awaitExchange it.awaitBody(Array<User>::class)
                  else throw Exception("Error while fetching users")
              }

    }

    suspend fun findUserById(id: String): User?{
        logger.info("findUserById $id")
            return client.get()
                .uri("${adminUrl}/users/$id")
                .awaitExchange {
                    if (it.statusCode().is2xxSuccessful) return@awaitExchange it.awaitBody(User::class)
                    else throw Exception("user $id Not found")
                }

    }

    suspend fun findUserByEmail(email: String): User? {
        val users = client.get()
            .uri {
                it.path("${adminUrl}/users")
                    .queryParam("email", email)
                    .queryParam("exact", true)
                    .build()
            }.awaitExchange {
                if (it.statusCode().is2xxSuccessful) return@awaitExchange it.awaitBody(Array<User>::class)
                else throw Exception("user Not found")
            }

        if (users.isEmpty()) return null

        return users[0]
    }


}