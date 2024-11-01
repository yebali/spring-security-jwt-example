package com.yebali.auth.member.service

import com.yebali.auth.jwt.service.JwtService
import com.yebali.auth.jwt.service.command.IssueJwt
import com.yebali.auth.member.entity.Member
import com.yebali.auth.member.repository.MemberRepository
import com.yebali.auth.member.service.command.SignIn
import com.yebali.auth.member.service.command.SignUp
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class MemberService(
    private val memberRepository: MemberRepository,
    private val jwtService: JwtService,
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
        val jwt = jwtService.issueJwt(
            IssueJwt.Command(
                username = command.username,
                password = command.password,
            ),
        )

        return SignIn.Result(jwt = jwt)
    }
}
