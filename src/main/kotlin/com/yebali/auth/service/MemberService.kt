package com.yebali.auth.service

import com.yebali.auth.entity.Member
import com.yebali.auth.jwt.JwtProvider
import com.yebali.auth.repository.MemberRepository
import com.yebali.auth.service.command.SignIn
import com.yebali.auth.service.command.SignUp
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class MemberService(
    private val memberRepository: MemberRepository,
    private val authenticationManagerBuilder: AuthenticationManagerBuilder,
    private val jwtProvider: JwtProvider,
) {
    fun signUp(command: SignUp.Command): SignUp.Result {
        val member = memberRepository.save(
            Member(
                username = command.username,
                password = command.password,
                nickname = command.nickname,
                roles = listOf("USER"),
            ),
        )

        return SignUp.Result(
            username = member.username,
            nickname = member.nickname,
        )
    }

    fun signIn(command: SignIn.Command): SignIn.Result {
        val authenticationToken = UsernamePasswordAuthenticationToken(command.username, command.password)
        val authentication = authenticationManagerBuilder.`object`.authenticate(authenticationToken)

        return SignIn.Result(token = jwtProvider.generateToken(authentication))
    }
}
