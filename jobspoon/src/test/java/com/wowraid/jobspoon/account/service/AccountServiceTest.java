package com.wowraid.jobspoon.account.service;

import com.wowraid.jobspoon.account.entity.Account;
import com.wowraid.jobspoon.account.entity.WithdrawalMembership;
import com.wowraid.jobspoon.account.repository.AccountCustomRepository;
import com.wowraid.jobspoon.account.repository.AccountRepository;
import com.wowraid.jobspoon.account.repository.WithdrawalMembershipRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @InjectMocks
    private AccountServiceImpl accountService;

    @Mock
    private AccountCustomRepository accountCustomRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private WithdrawalMembershipRepository withdrawalRepository;

    @Test
    @DisplayName("createAccount() - CustomRepository 호출 확인")
    void createAccount_success() {
        //given
        String email = "mock@test.com";
        String loginType = "GOOGLE";

        //when
        accountService.createAccount(email, loginType);

        //then
        verify(accountCustomRepository, times(1)).save(email, loginType);
    }

    @Test
    @DisplayName("createGuestAccount() - 게스트 계정 생성")
    void createGuestAccount_success() {
        //given
        String email = "guest1@example.com";
        String loginType = "GOOGLE";

        //when
        accountService.createGuestAccount(email, loginType);

        //then
        verify(accountCustomRepository, times(1)).save(email, loginType);
    }

    @Test
    @DisplayName("createAdminAccount() - 관리자 계정 생성")
    void createAdminAccount_success() {
        //given
        String email = "admin@example.com";
        String loginType = "GOOGLE";

        //when
        accountService.createAdminAccount(email, loginType);

        //then
        verify(accountCustomRepository, times(1)).saveAdmin(email, loginType);
    }

    @Test
    @DisplayName("checkEmailDuplication() - 중복이면 true")
    void checkEmailDuplication_true() {
        //given
        String email = "dup@test.com";
        when(accountRepository.existsByEmail(email)).thenReturn(true);

        //when
        boolean result = accountService.checkEmailDuplication(email);

        //then
        assertThat(result).isTrue();
        verify(accountRepository).existsByEmail(email);
    }

    @Test
    @DisplayName("checkEmailDuplication() - 중복 아니면 false")
    void checkEmailDuplication_false() {
        //given
        String email = "new@test.com";
        when(accountRepository.existsByEmail(email)).thenReturn(false);

        //when
        boolean result = accountService.checkEmailDuplication(email);

        //then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("findEmail() - accountId로 이메일 조회")
    void findEmail_success() {
        //given
        Long accountId = 1L;
        Account mockAccount = Account.builder().id(accountId).email("found@test.com").build();
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(mockAccount));

        //when
        String email = accountService.findEmail(accountId);

        //then
        assertThat(email).isEqualTo("found@test.com");
    }

    @Test
    @DisplayName("createWithdrawAccount() - 탈퇴 정보 저장")
    void createWithdrawAccount_success() {
        //given
        String accountId = "123";

        //when
        accountService.createWithdrawAccount(accountId);

        //then
        verify(withdrawalRepository).save(any(WithdrawalMembership.class));
    }

    @Test
    @DisplayName("createWithdrawAt() - 탈퇴 시각 업데이트")
    void createWithdrawAt_success() {
        //given
        String accountId = "456";
        LocalDateTime time = LocalDateTime.now();
        WithdrawalMembership wm = new WithdrawalMembership(accountId, null, null);
        when(withdrawalRepository.findByAccountId(accountId)).thenReturn(Optional.of(wm));

        //when
        accountService.createWithdrawAt(accountId, time);

        //then
        assertThat(wm.getWithdrawAt()).isEqualTo(time);
        verify(withdrawalRepository).save(wm);
    }

    @Test
    @DisplayName("createWithdrawEnd() - 탈퇴 만료 시각 +3년 설정")
    void createWithdrawEnd_success() {
        //given
        String accountId = "789";
        LocalDateTime now = LocalDateTime.of(2025, 7, 29, 9, 0);
        WithdrawalMembership wm = new WithdrawalMembership(accountId, null, null);
        when(withdrawalRepository.findByAccountId(accountId)).thenReturn(Optional.of(wm));

        //when
        accountService.createWithdrawEnd(accountId, now);

        //then
        assertThat(wm.getWithdrawEnd()).isEqualTo(now.plusYears(3));
        verify(withdrawalRepository).save(wm);
    }

    @Test
    @DisplayName("withdraw() - CustomRepository delete 호출")
    void withdraw_success() {
        //given
        String accountId = "999";
        when(accountCustomRepository.deleteAccount(accountId)).thenReturn(true);

        //when
        boolean result = accountService.withdraw(accountId);

        //then
        assertThat(result).isTrue();
        verify(accountCustomRepository).deleteAccount(accountId);
    }

    @Test
    @DisplayName("countEmail() - guest 접두어 개수 조회")
    void countEmail_success() {
        //given
        String prefix = "guest_";
        when(accountRepository.countByEmailStartingWith(prefix)).thenReturn(5L);

        //when
        long count = accountService.countEmail(prefix);

        //then
        assertThat(count).isEqualTo(5L);
    }
}
