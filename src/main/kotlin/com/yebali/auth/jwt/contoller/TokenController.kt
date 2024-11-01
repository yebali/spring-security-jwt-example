package com.yebali.auth.jwt.contoller

import com.yebali.auth.jwt.contoller.rest.RefreshTokenRest
import com.yebali.auth.jwt.service.JwtService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class TokenController(
    private val jwtService: JwtService,
) {
    @PostMapping("/refresh-token")
    fun refreshToken(
        @RequestBody request: RefreshTokenRest.Request,
    ): RefreshTokenRest.Response {
        val jwtToken = jwtService.renewJwt(request.refreshToken)

        return RefreshTokenRest.Response(
            accessToken = jwtToken.accessToken,
            refreshToken = jwtToken.refreshToken,
        )
    }
}
