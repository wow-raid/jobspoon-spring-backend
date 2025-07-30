package com.wowraid.jobspoon.account.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "account")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 32, nullable = false, unique = true)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_type_id", nullable = false)
    private AccountRoleType roleType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "login_type_id", nullable = false)
    private AccountLoginType loginType;

    public Account(String email, AccountRoleType roleType, AccountLoginType loginTypeEntity) {
        this.email = email;
        this.roleType = roleType;
        this.loginType = loginTypeEntity;
    }
}
