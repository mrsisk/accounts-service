package mrsisk.github.io.accountsmanager.security

import org.springframework.core.convert.converter.Converter
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt

internal class GrantedAuthoritiesExtractor : Converter<Jwt, Collection<GrantedAuthority>> {
    override fun convert(jwt: Jwt): Collection<GrantedAuthority> {

        val authorities = (jwt.claims
            .getOrDefault("realm_access", emptyMap<String, Any>()) as Map<*, *>)

        val roles = (authorities["roles"] ?: emptyList<String>()) as List<*>

        return roles
            .map { it.toString() }
            .map { SimpleGrantedAuthority(it) }
    }
}