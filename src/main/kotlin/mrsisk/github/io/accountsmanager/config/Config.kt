package mrsisk.github.io.accountsmanager.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.oauth2.client.*
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono


@Configuration
class Config {
    @Value("\${auth-server.base-uri}")
    lateinit var baseUri: String

    @Bean
    fun webClient(authorizedClientManager: ReactiveOAuth2AuthorizedClientManager): WebClient {
        val oauth2Client = ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager)
        oauth2Client.setDefaultClientRegistrationId("keycloak")
        return WebClient.builder()
            .filter(oauth2Client)
            .baseUrl(baseUri)
            .build()
    }

    @Bean
    fun authorizedClientManager(
        clientRegistrationRepository: ReactiveClientRegistrationRepository,
        authorizedClientRepository: ReactiveOAuth2AuthorizedClientService
    ): ReactiveOAuth2AuthorizedClientManager {
        val authorizedClientProvider: ReactiveOAuth2AuthorizedClientProvider = ReactiveOAuth2AuthorizedClientProviderBuilder.builder()
            .clientCredentials()
            .authorizationCode()
            .password()
            .refreshToken()
            .build()
        val authorizedClientManager = AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(
            clientRegistrationRepository, authorizedClientRepository)
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider)
        return authorizedClientManager
    }

    @Bean
    fun userDetailedService(): ReactiveUserDetailsService = ReactiveUserDetailsService {username: String? ->
        Mono.just(User(username, "", emptyList()))
    }
}

