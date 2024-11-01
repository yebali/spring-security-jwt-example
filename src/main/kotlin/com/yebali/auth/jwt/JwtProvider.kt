package com.yebali.auth.jwt

import com.yebali.auth.entity.Member
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Component
import java.util.Date

@Component
class JwtProvider(
    @Value("\${jwt.secret}")
    private val secret: String,
) {
    private val key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret))

    // 토큰 생성
    fun generateToken(authentication: Authentication, member: Member): JwtToken {
        // 권한 가져오기
        val authorities = authentication.authorities.joinToString { it.authority }

        val accessToken = Jwts
            .builder()
            .subject(authentication.name)
            .claim(JwtClaim.AUTH.value, authorities)
            .claim(JwtClaim.NICKNAME.value, member.nickname)
            .expiration(Date(Date().time + 864000000))
            .signWith(key)
            .compact()

        val refreshToken = Jwts
            .builder()
            .expiration(Date(Date().time + 864000000))
            .signWith(key)
            .compact()

        return JwtToken(
            grantType = "Bearer",
            accessToken = accessToken,
            refreshToken = refreshToken,
        )
    }

    fun getAuthentication(accessToken: String): Authentication {
        val claims = parseClaims(accessToken)
        val authorities = claims[JwtClaim.AUTH.value]
            .toString()
            .split(",")
            .map { SimpleGrantedAuthority(it) }

        return UsernamePasswordAuthenticationToken(
            User(claims.subject, "", authorities),
            null,
            authorities,
        )
    }

    fun parseClaims(accessToken: String): Claims {
        return Jwts.parser()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(accessToken)
            .payload
    }

    // Filter에서 사용
    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)

            true
        } catch (e: Exception) {
            false
        }
    }
}
