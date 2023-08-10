package mrsisk.github.io.accountsmanager.controllers

import mrsisk.github.io.accountsmanager.models.LoginRequest
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.WebUtils



@RestController
@RequestMapping("/api/test")
class TestController {

    @GetMapping("/token")
    fun test(): ResponseEntity<Map<String, String>>{
        val cookie = ResponseCookie.from("refresh_token", "Testdata")
            .httpOnly(false)
            .maxAge(30 * 60 * 1000)
            .build()

        return  ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(mapOf("token" to "test data"))
    }

    @PostMapping("/post")
    fun post(@RequestBody data: LoginRequest, request: ServerHttpRequest): Map<String, String> {
        val cookie = request.cookies["refresh_token"]

        println("RECIVED --------- $cookie")
        return mapOf("data" to "test")
    }
}