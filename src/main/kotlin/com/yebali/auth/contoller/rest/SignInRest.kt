package com.yebali.auth.contoller.rest

import com.yebali.auth.jwt.JwtToken

interface SignInRest {
    data class Request(
        val username: String,
        val password: String,
    )

    data class Response(
        val token: JwtToken,
    )
}
