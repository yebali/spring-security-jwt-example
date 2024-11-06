package com.yebali.auth.config

import com.yebali.auth.filter.JwtAuthenticationFilter
import com.yebali.auth.jwt.service.JwtService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class WebSecurityConfig(
    private val jwtService: JwtService,
) {
    @Bean
    fun filterChain(httpSecurity: HttpSecurity): SecurityFilterChain =
        httpSecurity
            .httpBasic { it.disable() }
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it
                    .requestMatchers(*EXCLUDE_PATH.toTypedArray()).permitAll()
                    .requestMatchers("/member/test").hasRole("USER")
                    .requestMatchers("/admin/test").hasRole("ADMIN")
                    .anyRequest().permitAll()
            }.addFilterBefore(
                JwtAuthenticationFilter(jwtService = jwtService, excludePaths = EXCLUDE_PATH),
                UsernamePasswordAuthenticationFilter::class.java,
            )
            .build()

    companion object {
        // 회원가입, 로그인, 토큰 갱신은 토큰 검증을 하지 않는다.
        private val EXCLUDE_PATH = listOf("/member/sign-up", "/member/sign-in", "/refresh-token")
    }
}
