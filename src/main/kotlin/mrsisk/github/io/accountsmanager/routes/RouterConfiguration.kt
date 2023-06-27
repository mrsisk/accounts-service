package mrsisk.github.io.accountsmanager.routes

import mrsisk.github.io.accountsmanager.handlers.AccountServiceHandler
import mrsisk.github.io.accountsmanager.handlers.AuthenticationServiceHandler
import mrsisk.github.io.accountsmanager.handlers.UserServiceHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class RouterConfiguration {

    @Bean
    fun adminRoutes(handler: UserServiceHandler) = coRouter {
        GET("/admin/users", handler::findAllUsers)
        GET("/admin/users/{id}", handler::getUserById)
    }

    @Bean
    fun accountRoutes(handler: AccountServiceHandler) = coRouter {
        POST("/user/register", handler::register)
    }

    @Bean
    fun authRoutes(handler: AuthenticationServiceHandler) = coRouter {
        POST("/auth/login", handler::login)
        POST("/auth/refresh", handler::refresh)
    }
}