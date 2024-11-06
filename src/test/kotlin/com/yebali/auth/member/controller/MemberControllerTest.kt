package com.yebali.auth.member.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.jayway.jsonpath.JsonPath
import com.yebali.auth.member.controller.rest.SignInRest
import com.yebali.auth.member.controller.rest.SignUpRest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@ActiveProfiles("test")
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
        signUp("yebali2", "1234")

        // 로그인
        signIn("yebali2", "1234")
            .andExpect(jsonPath("$.accessToken").exists())
            .andExpect(jsonPath("$.refreshToken").exists())
            .andExpect(status().isOk)
    }

    @Test
    fun `USER 권한을 가진 사용자는 USER을 위한 경로에 접근할 수 있다`() {
        signUp(username = "yebali3", password = "1234")
        val accessToken = signIn("yebali3", "1234")
            .andReturn()
            .response
            .contentAsString
            .let { JsonPath.parse(it).read<String>("$.accessToken") }

        mockMvc.perform(
            get("/member/test")
                .apply { servletPath("/member/test") }
                .header("Authorization", accessToken),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").value("yebali3"))
    }

    @Test
    fun `USER 권한을 가진 사용자는 ADMIN을 위한 경로에 접근할 수 없다`() {
        signUp(username = "yebali4", password = "1234")
        val accessToken = signIn("yebali4", "1234")
            .andReturn()
            .response
            .contentAsString
            .let { JsonPath.parse(it).read<String>("$.accessToken") }

        mockMvc.perform(
            get("/admin/test")
                .apply { servletPath("/admin/test") }
                .header("Authorization", accessToken),
        ).andExpect(status().isForbidden)
    }

    private fun signUp(username: String, password: String, nickname: String = ""): ResultActions {
        return mockMvc.perform(
            post("/member/sign-up")
                .apply { servletPath("/member/sign-up") }
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

    private fun signIn(username: String, password: String): ResultActions {
        return mockMvc.perform(
            post("/member/sign-in")
                .apply { servletPath("/member/sign-in") }
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        SignInRest.Request(
                            username = username,
                            password = password,
                        ),
                    ),
                ),
        )
    }
}
