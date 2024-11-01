package com.yebali.auth.member.service.command

import com.yebali.auth.jwt.Jwt

interface SignIn {
    data class Command(
        val username: String,
        val password: String,
    )

    data class Result(
        val jwt: Jwt,
    )
}
