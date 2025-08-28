package com.listik.userservice.config

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class ServiceAuthenticationFilter(
    private val serviceAuthSecret: String
) : OncePerRequestFilter() {

    companion object {
        private const val SERVICE_AUTH_HEADER = "X-Service-Auth"
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val serviceAuthHeader = request.getHeader(SERVICE_AUTH_HEADER)
        
        if (serviceAuthHeader == serviceAuthSecret) {
            // Create authentication for internal service calls
            val authentication = UsernamePasswordAuthenticationToken(
                "internal-service",
                null,
                listOf(SimpleGrantedAuthority("ROLE_SERVICE"))
            )
            SecurityContextHolder.getContext().authentication = authentication
        }
        
        filterChain.doFilter(request, response)
    }
}