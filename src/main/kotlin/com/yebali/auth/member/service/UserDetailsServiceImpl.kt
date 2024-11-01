package com.yebali.auth.member.service

import com.yebali.auth.member.entity.Member
import com.yebali.auth.member.repository.MemberRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserDetailsServiceImpl(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
) : UserDetailsService {
    // Spring Security가 AuthenticationToken을 인증할 때 호출한다.
    override fun loadUserByUsername(username: String): UserDetails {
        return memberRepository.findByUsername(username)
            ?.let { createUserDetails(it) }
            ?: throw IllegalArgumentException("User not found")
    }

    // Spring Security에서 Password를 다룰 땐 Encoding 해야 한다.
    private fun createUserDetails(member: Member): UserDetails {
        return User.builder()
            .username(member.username)
            .password(passwordEncoder.encode(member.password))
            .roles(*member.roles.toTypedArray())
            .build()
    }
}
