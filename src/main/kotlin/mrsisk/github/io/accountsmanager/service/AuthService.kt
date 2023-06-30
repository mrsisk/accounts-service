package mrsisk.github.io.accountsmanager.service

import mrsisk.github.io.accountsmanager.models.AuthError
import mrsisk.github.io.accountsmanager.models.AuthenticationResponse
import mrsisk.github.io.accountsmanager.models.Result
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.awaitExchange

@Service
class AuthService(private val client: WebClient) {

    @Value("\${accounts-manager.oauth.client-id}")
    lateinit var clientId: String

    @Value("\${accounts-manager.oauth.client-secret}")
    lateinit var clientSecret: String

    @Value("\${auth-server.token-uri}")
    lateinit var tokenUrl: String


    suspend fun authenticate(email: String, password: String): Result<AuthenticationResponse, AuthError>{

        val formData = LinkedMultiValueMap<String, String>()
        formData.add("username", email)
        formData.add("password", password)
        formData.add("grant_type", "password")
        formData.add("client_id", clientId)
        formData.add("client_secret", clientSecret)
        formData.add("scopes", "offline_access,openid")

        try {
            return request(formData)
        } catch (ex: Exception) {
            println("Error: " + ex.localizedMessage)
            throw ex
        }

    }

    private suspend fun request(formData: LinkedMultiValueMap<String, String>): Result<AuthenticationResponse, AuthError>{
        return client.post()
            .uri(tokenUrl)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .accept(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromFormData(formData))
            .awaitExchange {
                if (it.statusCode().is2xxSuccessful){
                    val tokens = it.awaitBody(AuthenticationResponse::class)
                    return@awaitExchange Result.Success<AuthenticationResponse>(tokens)
                }
                else{
                    val errorBody = it.awaitBody<AuthError>()
                    return@awaitExchange Result.Error(errorBody)
                }
            }
    }
    suspend fun refresh(token: String): Result<AuthenticationResponse, AuthError>{
        val formData = LinkedMultiValueMap<String, String>()
        formData.add("refresh_token", token)
        formData.add("grant_type", "refresh_token")
        formData.add("client_id", clientId)
        formData.add("client_secret", clientSecret)
        try {
            return request(formData)
        }catch (ex: Exception){
            println("Error: " + ex.localizedMessage)
            throw ex
        }
    }




}