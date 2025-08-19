package com.wowraid.jobspoon.accountProfile.entity;


import com.wowraid.jobspoon.account.entity.Account;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "account_profile")
public class AccountProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", referencedColumnName = "id", nullable = false)
    private Account account;

    @Column(name = "nickname", nullable = false, unique = true, length = 30)
    private String nickname;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;


}
