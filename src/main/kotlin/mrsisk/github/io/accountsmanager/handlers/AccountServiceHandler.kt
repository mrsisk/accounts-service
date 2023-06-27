package mrsisk.github.io.accountsmanager.handlers

import kotlinx.coroutines.reactive.awaitFirst
import mrsisk.github.io.accountsmanager.models.RegistrationRequest
import mrsisk.github.io.accountsmanager.service.RegistrationService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.awaitEntity
import org.springframework.web.reactive.function.server.*

@Component
class AccountServiceHandler(private val registrationService: RegistrationService) {


    suspend fun register(serverRequest: ServerRequest): ServerResponse {
        val body = serverRequest.bodyToMono<RegistrationRequest>().awaitFirst()
        val clientResponse = registrationService.registerUser(body)
        val message  = clientResponse.awaitEntity<String>()
        return if (clientResponse.statusCode().is2xxSuccessful) ServerResponse.status(clientResponse.statusCode()).buildAndAwait()
        else ServerResponse.status(clientResponse.statusCode()).bodyValueAndAwait(mapOf("error" to message.body))
    }



}