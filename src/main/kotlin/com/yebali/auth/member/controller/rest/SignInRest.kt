package com.yebali.auth.member.controller.rest

interface SignInRest {
    data class Request(
        val username: String,
        val password: String,
    )

    data class Response(
        val accessToken: String,
        val refreshToken: String,
    )
}
