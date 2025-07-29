package com.wowraid.jobspoon.account.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

@Entity
@Table(name = "account_role_type")
@Getter
@ToString
public class AccountRoleType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //컬럼명 건드리지 말기
    @Enumerated(EnumType.STRING)
    @Column(name = "role_type", length = 64, nullable = false)
    private RoleType roleEnum;

    protected AccountRoleType() {}

    public AccountRoleType(RoleType roleEnum) {
        this.roleEnum = roleEnum;
    }
}
