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
        GET("/auth/admin/users", handler::findAllUsers)
        GET("/auth/admin/users/{id}", handler::getUserById)
    }

    @Bean
    fun accountRoutes(handler: AccountServiceHandler) = coRouter {
        POST("/auth/user/register", handler::register)
    }

    @Bean
    fun authRoutes(handler: AuthenticationServiceHandler) = coRouter {
        POST("/auth/login", handler::login)
        POST("/auth/refresh", handler::refresh)
        GET("/auth/userinfo", handler::userInfo)
        POST("/auth/signout", handler::signOut)
    }


}