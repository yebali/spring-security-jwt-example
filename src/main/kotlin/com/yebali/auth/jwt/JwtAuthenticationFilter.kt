package com.yebali.auth.jwt

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class JwtAuthenticationFilter(
    private val jwtProvider: JwtProvider,
) : OncePerRequestFilter() {
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val excludedPaths = listOf("/sign-up", "/sign-in")

        // 로그인, 회원가입은 토큰 검증 하지 않는다.
        if (excludedPaths.any { request.servletPath == it }) {
            filterChain.doFilter(request, response)
            return
        }

        val token = extractToken(request)

        val authentication = jwtProvider.getAuthentication(token)
        SecurityContextHolder.getContext().authentication = authentication

        filterChain.doFilter(request, response)
    }

    private fun extractToken(request: HttpServletRequest): String {
        val token = request.getHeader("Authorization")

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
