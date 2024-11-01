package com.yebali.auth.jwt.service

import com.yebali.auth.jwt.service.command.IssueJwt
import com.yebali.auth.member.service.MemberService
import com.yebali.auth.member.service.command.SignUp
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class JwtServiceTest {
    @Autowired
    private lateinit var jwtService: JwtService

    @Autowired
    private lateinit var memberService: MemberService

    @Test
    fun `generate jwt from refresh token`() {
        signUp("yebali", "1234", "예발이")

        val originalJwt = jwtService.issueJwt(
            command = IssueJwt.Command(username = "yebali", password = "1234"),
        )

        val renewedJwt = assertDoesNotThrow {
            jwtService.renewJwt(originalJwt.refreshToken)
        }

        Assertions.assertThat(renewedJwt.accessToken).isNotEqualTo(originalJwt.accessToken)
        Assertions.assertThat(renewedJwt.refreshToken).isNotEqualTo(originalJwt.refreshToken)
    }

    private fun signUp(username: String, password: String, nickname: String = "") {
        memberService.signUp(
            SignUp.Command(
                username = username,
                password = password,
                nickname = nickname,
            ),
        )
    }
}
