package com.yebali.auth.jwt.service

import com.yebali.auth.jwt.Jwt
import com.yebali.auth.jwt.JwtClaim
import com.yebali.auth.jwt.entity.RefreshToken
import com.yebali.auth.jwt.repository.RefreshTokenRepository
import com.yebali.auth.jwt.service.command.IssueJwt
import com.yebali.auth.member.repository.MemberRepository
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Date

@Service
@Transactional
class JwtService(
    @Value("\${jwt.access-secret}")
    private val accessSecret: String,
    @Value("\${jwt.refresh-secret}")
    private val refreshSecret: String,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val memberRepository: MemberRepository,
    private val authenticationManagerBuilder: AuthenticationManagerBuilder,
) {
    private val accessSecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessSecret))
    private val refreshSecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshSecret))

    // 토큰 생성
    fun issueJwt(command: IssueJwt.Command): Jwt {
        val member = memberRepository.findByUsername(command.username)
            ?: throw IllegalArgumentException("Member not found")

        val authenticationToken = UsernamePasswordAuthenticationToken(command.username, command.password)
        val authentication = authenticationManagerBuilder.`object`.authenticate(authenticationToken)
        val now = System.currentTimeMillis()

        val accessToken = Jwts
            .builder()
            .subject(authentication.name)
            .claims(
                mapOf(
                    JwtClaim.USERNAME.value to member.username,
                    JwtClaim.NICKNAME.value to member.nickname,
                    JwtClaim.AUTH.value to authentication.authorities.joinToString { it.authority },
                ),
            )
            .expiration(Date(now + ACCESS_TOKEN_EXPIRATION))
            .signWith(accessSecretKey)
            .compact()

        val refreshToken = Jwts
            .builder()
            .expiration(Date(now + REFRESH_TOKEN_EXPIRATION))
            .signWith(refreshSecretKey)
            .compact()

        // RefreshToken 저장
        refreshTokenRepository.save(
            RefreshToken(refreshToken = refreshToken, username = member.username),
        )

        return Jwt(
            accessToken = "Bearer $accessToken",
            refreshToken = refreshToken,
        )
    }

    // 토큰 갱신
    fun renewJwt(refreshToken: String): Jwt {
        val refreshTokenEntity = refreshTokenRepository.findByRefreshToken(refreshToken)
            ?: throw IllegalArgumentException("Refresh token is not valid")

        val member = memberRepository.findByUsername(refreshTokenEntity.username)
            ?: throw IllegalArgumentException("Member not found")

        refreshTokenRepository.delete(refreshTokenEntity)

        return issueJwt(
            IssueJwt.Command(
                username = member.username,
                password = member.password,
            ),
        )
    }

    fun getAuthentication(accessToken: String): Authentication {
        val claims = parseClaims(accessToken)
        val username = claims[JwtClaim.USERNAME.value].toString()
        val authorities = claims[JwtClaim.AUTH.value]
            .toString()
            .split(",")
            .map { SimpleGrantedAuthority(it) }

        return UsernamePasswordAuthenticationToken(
            User(username, "", authorities),
            null,
            authorities,
        )
    }

    private fun parseClaims(accessToken: String): Claims {
        return Jwts.parser()
            .verifyWith(accessSecretKey)
            .build()
            .parseSignedClaims(accessToken)
            .payload
    }

    companion object {
        private const val ACCESS_TOKEN_EXPIRATION = 86400000L
        private const val REFRESH_TOKEN_EXPIRATION = 86400000L
    }
}
