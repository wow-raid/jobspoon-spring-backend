package com.wowraid.jobspoon.account.repository;

/*
통합 테스트입니다.
 */

import com.wowraid.jobspoon.account.entity.Account;
import com.wowraid.jobspoon.account.entity.enums.RoleType;
import com.wowraid.jobspoon.account.entity.WithdrawalMembership;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class AccountRepositoryTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountCustomRepository accountCustomRepository;

    @Autowired
    private WithdrawalMembershipRepository withdrawalMembershipRepository;

    @Test
    @DisplayName("save() : NORMAL 사용자")
    void save_normalUser(){
        //given
        String email = "user1@test.com";
        String loginType = "GOOGLE";

        //when
        accountCustomRepository.save(email, loginType);

        //then
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("계정이 저장되지 않았습니다."));

        assertThat(account.getLoginType().getLoginType().name()).isEqualTo(loginType);
        assertThat(account.getRoleType().getRoleEnum()).isEqualTo(RoleType.NORMAL);
    }

    @Test
    @DisplayName("saveAdmin() : ADMIN 사용자")
    void save_adminUser(){
        //given
        String email = "user2@test.com";
        String loginType = "KAKAO";

        //when
        accountCustomRepository.saveAdmin(email, loginType);

        //then
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("계정이 저장되지 않았습니다."));

        //name()으로 enum을 문자열로 반환
        assertThat(account.getLoginType().getLoginType().name()).isEqualTo(loginType);
        //둘 다 enum 타입
        assertThat(account.getRoleType().getRoleEnum()).isEqualTo(RoleType.ADMIN);
    }

    @Test
    @DisplayName("같은 이메일이라도 메소드에 따라 권한이 달라짐")
    void sameEmailDifferentMethods(){
        //given
        String email = "user3@test.com";

        //when(이메일 중복 저장 안 되지만, 상황 가정!)
        accountCustomRepository.save(email, "GOOGLE");
        Account savedNormal = accountRepository.findByEmail(email).orElseThrow();
        Long accountId = savedNormal.getId();
        accountRepository.deleteById(accountId);
        em.flush();

        accountCustomRepository.saveAdmin(email, "GOOGLE");

        //then
        Account savedAdmin = accountRepository.findByEmail(email).orElseThrow();
        assertThat(savedAdmin.getRoleType().getRoleEnum()).isEqualTo(RoleType.ADMIN);
    }

    @Test
    @DisplayName("delete() : 계정 삭제")
    void deleteAccount_success_and_fail(){
        String email = "delme@example.com";
        accountCustomRepository.save(email, "GOOGLE");
        Long id = accountRepository.findByEmail(email).orElseThrow().getId();

        boolean deleted = accountCustomRepository.deleteAccount(id.toString());
        assertThat(deleted).isTrue();
        assertThat(accountRepository.findById(id)).isEmpty();

        boolean fail = accountCustomRepository.deleteAccount("notanumber");
        assertThat(fail).isFalse();
    }

    @Test
    @DisplayName("탈퇴 시각 및 만료일 저장 테스트")
    void saveWithdrawAt_and_End(){
        String accId = "uuid-user-456";
        accountCustomRepository.saveWithdrawInfo(accId);
        LocalDateTime now = LocalDateTime.now();

        accountCustomRepository.saveWithdrawAt(accId, now);
        accountCustomRepository.saveWithdrawEnd(accId, now);

        WithdrawalMembership found = withdrawalMembershipRepository.findByAccountId(accId).orElseThrow();
        assertThat(found.getWithdrawAt()).isEqualTo(now);
        assertThat(found.getWithdrawEnd()).isEqualTo(now.plusYears(3));
    }

    @Test
    @DisplayName("이메일 존재 여부 및 접두사 카운트")
    void existByEmail_and_countByPrefix(){
        accountCustomRepository.save("abc_1@example.com", "GOOGLE");
        accountCustomRepository.save("abc_2@example.com", "GOOGLE");
        accountCustomRepository.save("def@example.com", "GOOGLE");

        assertThat(accountRepository.existsByEmail("abc_1@example.com")).isTrue();
        assertThat(accountRepository.existsByEmail("zzz@example.com")).isFalse();
        assertThat(accountRepository.countByEmailStartingWith("abc")).isEqualTo(2);
    }
}
