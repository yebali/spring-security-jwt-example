package com.yebali.auth.jwt.repository

import com.yebali.auth.jwt.entity.RefreshToken
import org.springframework.data.jpa.repository.JpaRepository

interface RefreshTokenRepository : JpaRepository<RefreshToken, String> {
    fun findByRefreshToken(refreshToken: String): RefreshToken?
}
