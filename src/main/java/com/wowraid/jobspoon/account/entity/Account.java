package com.wowraid.jobspoon.account.entity;


import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_type_id", nullable = false)
    private AccountRoleType accountRoleType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "login_type_id", nullable = false)
    private AccountLoginType accountLoginType;

    public Account(Long id) {
        this.id = id;
    }

    public Account() {

    }

    public Account(AccountRoleType accountRoleType, AccountLoginType accountLoginType) {
        this.accountRoleType = accountRoleType;
        this.accountLoginType = accountLoginType;
    }
}
