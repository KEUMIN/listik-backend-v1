package com.listik.userservice.config

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class GatewayAuthenticationFilter : OncePerRequestFilter() {

    companion object {
        private const val USER_ID_HEADER = "X-User-Id"
        private const val USER_ROLES_HEADER = "X-User-Roles"
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val userId = request.getHeader(USER_ID_HEADER)
        val rolesHeader = request.getHeader(USER_ROLES_HEADER)

        if (userId != null && rolesHeader != null) {

            val authorities = rolesHeader.split(",")
                .map { SimpleGrantedAuthority(it) }

            val authentication = UsernamePasswordAuthenticationToken(
                userId,
                null,
                authorities
            )

            SecurityContextHolder.getContext().authentication = authentication
        }

        filterChain.doFilter(request, response)
    }
}