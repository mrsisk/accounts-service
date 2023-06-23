package mrsisk.github.io.accountsmanager

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.reactive.config.EnableWebFlux

@SpringBootApplication
class AccountsManagerApplication

fun main(args: Array<String>) {
	runApplication<AccountsManagerApplication>(*args)
}
