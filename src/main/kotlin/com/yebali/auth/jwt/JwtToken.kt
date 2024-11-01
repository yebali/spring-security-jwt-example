package com.yebali.auth.jwt

data class JwtToken(
    val grantType: String,
    val accessToken: String,
    val refreshToken: String,
)
