package com.yebali.auth.filter

import com.yebali.auth.jwt.service.JwtService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class JwtAuthenticationFilter(
    private val jwtService: JwtService,
    private val excludePaths: List<String>,
) : OncePerRequestFilter() {
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        if (excludePaths.any { request.servletPath == it }) {
            filterChain.doFilter(request, response)
            return
        }

        val token = request.extractToken()

        val authentication = jwtService.getAuthentication(token)
        SecurityContextHolder.getContext().authentication = authentication

        filterChain.doFilter(request, response)
    }

    private fun HttpServletRequest.extractToken(): String {
        val token = this.getHeader("Authorization")

        if (token == null || token.isBlank()) {
            throw IllegalArgumentException("Token is missing")
        }

        return if (token.startsWith("Bearer ")) {
            token.substring(7)
        } else {
            throw IllegalArgumentException("Token is not Bearer Token")
        }
    }
}
