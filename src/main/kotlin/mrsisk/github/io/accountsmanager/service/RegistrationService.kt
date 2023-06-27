package mrsisk.github.io.accountsmanager.service
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mrsisk.github.io.accountsmanager.events.ConfirmEmailEvent
import mrsisk.github.io.accountsmanager.models.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.*



@Service
class RegistrationService(
    private val client: WebClient,
    private val publisher: ApplicationEventPublisher,
    private val userService: UserService
) {
    @Value("\${auth-server.admin-uri}")
    lateinit var adminUrl: String
    companion object {
        private val logger = LoggerFactory.getLogger(RegistrationService::class.java)
    }
    suspend fun registerUser(body: RegistrationRequest): ClientResponse {
        logger.info("Registering user ${body.email} ")
        val user = RegistrationBody(
            email = body.email,
            firstName = body.firstName,
            lastName = body.lastName,
            username = body.email,
            credentials = listOf(Credentials(value = body.password, temporary = false)),
            requiredActions = mutableListOf("VERIFY_EMAIL")
        )
        return client.post()
            .uri("${adminUrl}/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(user)
            .awaitExchange {
                if (it.statusCode().is2xxSuccessful){
                    sentConfirmationEmail(body.email)
                    ClientResponse.create(HttpStatus.CREATED)
                }
                else if (it.statusCode() == HttpStatus.CONFLICT){
                    val error = it.awaitBody<KeyCloakError>()
                    return@awaitExchange ClientResponse.create(HttpStatus.CONFLICT).body(error.errorMessage)
                }
                else return@awaitExchange  ClientResponse.create(HttpStatus.BAD_REQUEST)
            }.build()

    }

    suspend fun sentConfirmationEmail(email: String){
        val user = userService.findUserByEmail(email) ?: return
        val event = ConfirmEmailEvent(email = user.username, id = user.id)
        publisher.publishEvent(event)
    }

    @Async
    @EventListener
   fun confirmEmailEvent(event: ConfirmEmailEvent){
        logger.info("Sending confirmation email event to {}", event.email)

        CoroutineScope(Dispatchers.IO).launch {
            val user = userService.findUserByEmail(event.email) ?: return@launch
            client.put()
                .uri("$adminUrl/users/${user.id}/execute-actions-email")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .awaitExchange {
                    if (it.statusCode().is2xxSuccessful){
                        logger.info("Successfully sent confirmation email request for  {}", user.username)
                    }else{
                        logger.error("Failed to sent confirmation email request for status ${it.statusCode()} {}", user.username)
                    }
                }

        }

    }
}
