package com.yebali.auth.member.controller

import com.yebali.auth.member.controller.rest.SignInRest
import com.yebali.auth.member.controller.rest.SignUpRest
import com.yebali.auth.member.service.MemberService
import com.yebali.auth.member.service.command.SignIn
import com.yebali.auth.member.service.command.SignUp
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/member")
class MemberController(
    private val memberService: MemberService,
) {
    @PostMapping("/sign-up")
    fun signUp(
        @RequestBody request: SignUpRest.Request,
    ): SignUpRest.Response {
        val result = memberService.signUp(
            SignUp.Command(
                username = request.username,
                password = request.password,
                nickname = request.nickname,
            ),
        )

        return SignUpRest.Response(
            username = result.username,
            nickname = result.nickname,
        )
    }

    @PostMapping("/sign-in")
    fun signIn(
        @RequestBody request: SignInRest.Request,
    ): SignInRest.Response {
        val result = memberService.signIn(
            SignIn.Command(
                username = request.username,
                password = request.password,
            ),
        )

        return SignInRest.Response(
            accessToken = result.jwt.accessToken,
            refreshToken = result.jwt.refreshToken,
        )
    }

    @GetMapping("/test")
    fun test(): String? {
        return SecurityContextHolder.getContext().authentication.name
    }
}
