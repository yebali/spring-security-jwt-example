package com.yebali.auth.jwt.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import java.util.UUID

/**
 * RefreshToken으로 AccessToken을 발행하기 위한 토큰 저장 테이블.
 * Redis에 만료시간 넣어서 저장하는게 더 좋아보임.
 * */
@Entity
@Table(indexes = [Index(name = "idx_refresh_token", columnList = "refreshToken")])
class RefreshToken(
    @Id
    val id: UUID = UUID.randomUUID(),
    val refreshToken: String,
    val username: String,
)
