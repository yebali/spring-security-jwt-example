package com.yebali.auth.service.command

interface SignUp {
    data class Command(
        val username: String,
        val password: String,
        val nickname: String,
    )

    data class Result(
        val username: String,
        val nickname: String,
    )
}
