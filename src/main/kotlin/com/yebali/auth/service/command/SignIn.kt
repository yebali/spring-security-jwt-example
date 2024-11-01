package com.yebali.auth.service.command

import com.yebali.auth.jwt.JwtToken

interface SignIn {
    data class Command(
        val username: String,
        val password: String,
    )

    data class Result(
        val token: JwtToken,
    )
}
