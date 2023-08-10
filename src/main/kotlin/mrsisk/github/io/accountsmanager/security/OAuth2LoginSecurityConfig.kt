package mrsisk.github.io.accountsmanager.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsConfigurationSource


import reactor.core.publisher.Mono


@Configuration
@EnableWebFluxSecurity
class OAuth2LoginSecurityConfig {

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {

        http.csrf().disable()
            .cors()
        //    .configurationSource(corsConfigurationSource)
            .and()
            .authorizeExchange { auth ->
            auth.pathMatchers("/auth/admin/**").hasAnyAuthority("service-account", "ADMIN")
                .pathMatchers("/auth/userinfo").authenticated()
                .anyExchange()
                .permitAll()
        }.oauth2Client(Customizer.withDefaults())

        http.oauth2Login()
        http.oauth2ResourceServer().jwt {
            it.jwtAuthenticationConverter(grantedAuthoritiesExtractor())
        }
        return http.build()

    }

    fun grantedAuthoritiesExtractor(): Converter<Jwt, Mono<AbstractAuthenticationToken>> {
        val jwtAuthenticationConverter = JwtAuthenticationConverter()
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(GrantedAuthoritiesExtractor())
        return ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter)
    }

    private val corsConfigurationSource = CorsConfigurationSource { request ->
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("http://localhost:4200", "http://localhost:8081")
        configuration.allowedMethods = mutableListOf(
            "GET",
            "POST",
            "DELETE",
            "UPDATE"
        )
        configuration.addAllowedHeader(CorsConfiguration.ALL)

        configuration.allowCredentials = true
        configuration
    }


}