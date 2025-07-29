package com.wowraid.jobspoon.account.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "account_login_type")
public class AccountLoginType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login_type", length = 10)
    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    protected AccountLoginType() {}

    public AccountLoginType(LoginType loginType) {
        this.loginType = loginType;
    }

    public Long getId() {
        return id;
    }

    public LoginType getLoginType() {
        return loginType;
    }

    @Override
    public String toString() {
        return loginType != null ? loginType.name() : "null";
    }
}
