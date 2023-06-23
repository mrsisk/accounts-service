package mrsisk.github.io.accountsmanager.routes

import mrsisk.github.io.accountsmanager.handlers.AccountsHandler
import mrsisk.github.io.accountsmanager.handlers.AuthenticationHandler
import mrsisk.github.io.accountsmanager.service.UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class RouterConfiguration {

    @Bean
    fun adminRoutes(userService: UserService) = coRouter {
        GET("/admin/users", userService::findAllUsers)
        GET("/admin/users/{id}", userService::findUser)
    }

    @Bean
    fun accountRoutes(handler: AccountsHandler) = coRouter {
        POST("/user/register", handler::register)
    }

    @Bean
    fun authRoutes(handler: AuthenticationHandler) = coRouter {
        POST("/auth/login", handler::login)
        POST("/auth/refresh", handler::refresh)
    }
}