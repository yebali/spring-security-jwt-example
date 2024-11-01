package com.yebali.auth.jwt

enum class JwtClaim(val value: String) {
    USERNAME("username"),
    AUTH("auth"),
    NICKNAME("nickname"),
}
