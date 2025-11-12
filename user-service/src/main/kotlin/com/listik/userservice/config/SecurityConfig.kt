package com.listik.userservice.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    @Value("\${service.auth.secret}") private val serviceAuthSecret: String
) {

    @Bean
    fun serviceAuthenticationFilter(): ServiceAuthenticationFilter {
        return ServiceAuthenticationFilter(serviceAuthSecret)
    }

    @Bean
    fun gatewayAuthenticationFilter(): GatewayAuthenticationFilter {
        return GatewayAuthenticationFilter()
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .addFilterBefore(serviceAuthenticationFilter(), UsernamePasswordAuthenticationFilter::class.java)
            .addFilterBefore(gatewayAuthenticationFilter(), UsernamePasswordAuthenticationFilter::class.java)
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/users/auth-account/**", "/users/create-with-auth").hasRole("SERVICE")
                    .requestMatchers("/users/me").hasRole("USER")
                    .requestMatchers("/actuator/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                    .anyRequest().denyAll()
            }

        return http.build()
    }
}