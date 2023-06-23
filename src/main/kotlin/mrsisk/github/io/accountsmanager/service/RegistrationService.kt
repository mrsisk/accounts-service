package mrsisk.github.io.accountsmanager.service
import mrsisk.github.io.accountsmanager.models.Credentials
import mrsisk.github.io.accountsmanager.models.KeyCloakError
import mrsisk.github.io.accountsmanager.models.RegistrationBody
import mrsisk.github.io.accountsmanager.models.RegistrationRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.*

@Service
class RegistrationService(
    private val client: WebClient
) {
    @Value("\${auth-server.admin-uri}")
    lateinit var adminUrl: String

    suspend fun registerUser(body: RegistrationRequest): ClientResponse {

        val user = RegistrationBody(
            email = body.email,
            firstName = body.firstName,
            lastName = body.lastName,
            enabled = true,
            username = body.email,
            credentials = listOf(Credentials(value = body.password, temporary = false))
        )
        return client.post()
            .uri("${adminUrl}/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(user)
            .awaitExchange {
                if (it.statusCode().is2xxSuccessful) ClientResponse.create(HttpStatus.CREATED)
                else if (it.statusCode() == HttpStatus.CONFLICT){
                    val error = it.awaitBody<KeyCloakError>()
                    return@awaitExchange ClientResponse.create(HttpStatus.CONFLICT).body(error.errorMessage)
                }
                else return@awaitExchange  ClientResponse.create(HttpStatus.BAD_REQUEST)
            }.build()

    }
}
