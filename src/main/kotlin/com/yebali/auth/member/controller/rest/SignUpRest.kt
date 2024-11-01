package com.yebali.auth.member.controller.rest

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
