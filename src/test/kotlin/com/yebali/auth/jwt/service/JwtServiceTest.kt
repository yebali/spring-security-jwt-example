package com.yebali.auth.jwt.service

import com.yebali.auth.jwt.service.command.IssueJwt
import com.yebali.auth.member.service.MemberService
import com.yebali.auth.member.service.command.SignUp
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class JwtServiceTest {
    @Autowired
    private lateinit var jwtService: JwtService

    @Autowired
    private lateinit var memberService: MemberService

    @Test
    fun `Refresh token으로 새로운 Access token 재발급`() {
        signUp("yebali", "1234", "예발이")

        val originalJwt = jwtService.issueJwt(
            command = IssueJwt.Command(username = "yebali", password = "1234"),
        )

        val renewedJwt = assertDoesNotThrow {
            // 같은 시간에 토큰이 발급되면 같은 값으로 발급된다.
            Thread.sleep(1000)
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
