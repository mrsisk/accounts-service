package mrsisk.github.io.accountsmanager.exception

import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest



@Component
class GlobalErrorAttributes : DefaultErrorAttributes() {

    override fun getErrorAttributes(request: ServerRequest?, options: ErrorAttributeOptions?): Map<String, Any> {

        val map: MutableMap<String, Any> = mutableMapOf()
        val err = getError(request)
       // map["status"] = HttpStatus.BAD_REQUEST
        map["message"] = err.localizedMessage
        println("yyyyyyyyyyyyyyyyyyyyyyyyyy $map")
        return map
    }
}

