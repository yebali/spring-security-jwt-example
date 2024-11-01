package com.yebali.auth.member.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.yebali.auth.member.controller.rest.SignInRest
import com.yebali.auth.member.controller.rest.SignUpRest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class MemberControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `회원가입`() {
        signUp(username = "yebali", password = "1234", nickname = "예발이")
            .andExpect(jsonPath("$.username").value("yebali"))
            .andExpect(jsonPath("$.nickname").value("예발이"))
            .andExpect(status().isOk)
    }

    @Test
    fun `로그인`() {
        // 회원가입
        signUp("yebali2", "4321", "예발이2")

        // 로그인
        mockMvc.perform(
            post("/sign-in")
                .apply { servletPath("/sign-in") }
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        SignInRest.Request(
                            username = "yebali2",
                            password = "4321",
                        ),
                    ),
                ),
        )
            .andExpect(jsonPath("$.accessToken").exists())
            .andExpect(jsonPath("$.refreshToken").exists())
            .andExpect(status().isOk)
    }

    private fun signUp(username: String, password: String, nickname: String = ""): ResultActions {
        return mockMvc.perform(
            post("/sign-up")
                .apply { servletPath("/sign-up") }
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        SignUpRest.Request(
                            username = username,
                            password = password,
                            nickname = nickname,
                        ),
                    ),
                ),
        )
    }
}
