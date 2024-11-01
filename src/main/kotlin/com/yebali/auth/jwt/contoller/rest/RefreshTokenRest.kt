package com.yebali.auth.jwt.contoller.rest

interface RefreshTokenRest {
    data class Request(
        val refreshToken: String,
    )

    data class Response(
        val accessToken: String,
        val refreshToken: String,
    )
}
