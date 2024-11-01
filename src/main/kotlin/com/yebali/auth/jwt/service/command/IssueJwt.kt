package com.yebali.auth.jwt.service.command

interface IssueJwt {
    data class Command(
        val username: String,
        val password: String,
    )
}
