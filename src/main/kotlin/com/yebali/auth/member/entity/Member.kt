package com.yebali.auth.member.entity

import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
@Table(
    indexes = [
        Index(columnList = "username", unique = true),
    ],
)
class Member(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    private val username: String,
    private val password: String,
    val nickname: String,
    @ElementCollection(fetch = FetchType.EAGER)
    val roles: List<String>,
) : UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = roles.map { role -> GrantedAuthority { role } }.toMutableList()

    override fun getPassword(): String = this.password

    override fun getUsername(): String = this.username
}
