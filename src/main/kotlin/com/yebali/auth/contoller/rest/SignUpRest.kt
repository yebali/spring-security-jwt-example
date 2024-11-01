package com.yebali.auth.contoller.rest

interface SignUpRest {
    data class Request(
        val username: String,
        val password: String,
        val nickname: String,
    )

    data class Response(
        val username: String,
        val nickname: String,
    )
}
