package com.yebali.auth.service

import com.yebali.auth.entity.Member
import com.yebali.auth.repository.MemberRepository
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
    override fun loadUserByUsername(username: String): UserDetails {
        return memberRepository.findByUsername(username)
            ?.let { createUserDetails(it) }
            ?: throw IllegalArgumentException("User not found")
    }

    // Spring Security에서 Password를 다룰 땐 Encoding 해야 한다.
    fun createUserDetails(member: Member): UserDetails {
        return User.builder()
            .username(member.username)
            .password(passwordEncoder.encode(member.password))
            .roles(*member.roles.toTypedArray())
            .build()
    }
}
