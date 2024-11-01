package com.yebali.auth.contoller

import com.yebali.auth.contoller.rest.SignInRest
import com.yebali.auth.contoller.rest.SignUpRest
import com.yebali.auth.service.MemberService
import com.yebali.auth.service.command.SignIn
import com.yebali.auth.service.command.SignUp
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
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
            token = result.token,
        )
    }
}
