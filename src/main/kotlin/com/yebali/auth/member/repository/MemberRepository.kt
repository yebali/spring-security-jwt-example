package com.yebali.auth.member.repository

import com.yebali.auth.member.entity.Member
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<Member, Long> {
    fun findByUsername(username: String): Member?
}
