package mrsisk.github.io.accountsmanager

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync


@EnableAsync
@SpringBootApplication
class AccountsManagerApplication

fun main(args: Array<String>) {
	runApplication<AccountsManagerApplication>(*args)
}
