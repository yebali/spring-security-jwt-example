package com.yebali.auth.jwt

data class Jwt(
    val accessToken: String,
    val refreshToken: String,
)
