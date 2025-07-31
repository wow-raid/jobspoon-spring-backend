package com.wowraid.jobspoon.account_profile.service;

import com.wowraid.jobspoon.account.entity.Account;
import com.wowraid.jobspoon.account.repository.AccountRepository;
import com.wowraid.jobspoon.account_profile.entity.AccountProfile;
import com.wowraid.jobspoon.account_profile.entity.AdminProfile;
import com.wowraid.jobspoon.account_profile.repository.AccountProfileRepository;
import com.wowraid.jobspoon.account_profile.service.request_form.AccountProfileRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class AccountProfileServiceTest {

    @InjectMocks
    private AccountProfileServiceImpl accountProfileService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountProfileRepository accountProfileRepository;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    private final Long accountId = 1L;
    private final String email = "tes@example.com";
    private final Account account = Account.builder()
            .id(accountId)
            .email(email)
            .build();
    private final AccountProfileRequest request =
            new AccountProfileRequest("곰돌이", "FEMALE", "1998", "20-29");

    @Nested
    @DisplayName("createAccountProfile()")
    class CreateAccountProfile {

        @Test
        @DisplayName("성공")
        void create_success(){
            //given
            when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

            //when
            boolean result = accountProfileService.createAccountProfile(accountId, request);

            //then
            assertThat(result).isTrue();
            verify(accountProfileRepository).save(account, "곰돌이", "FEMALE", "1998", "20-29");
        }
    }

    @Nested
    @DisplayName("updateAccountProfileExists()")
    class UpdateAccountProfile {

        @Test
        @DisplayName("기존 프로필이 있으면 수정")
        void update_existing_profile() {
            //given
            AccountProfile profile = mock(AccountProfile.class);
            when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
            when(accountProfileRepository.findByAccount(account)).thenReturn(Optional.of(profile));

            //when
            boolean result = accountProfileService.updateAccountProfileIfExists(accountId, request);

            //then
            assertThat(result).isTrue();
            verify(profile).updateProfile("곰돌이", "FEMALE", "1998", "20-29");
        }

        @Test
        @DisplayName("기존 프로필이 없으면 생성")
        void update_creates_if_absent(){
            //given
            when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
            when(accountProfileRepository.findByAccount(account)).thenReturn(Optional.empty());

            //when
            boolean result = accountProfileService.updateAccountProfileIfExists(accountId, request);

            //then
            assertThat(result).isTrue();
            verify(accountProfileRepository).save(
                    eq(account),
                    eq("곰돌이"),
                    eq("FEMALE"),
                    eq("1998"),
                    eq("20-29")
            );
        }
    }

    @Nested
    @DisplayName("createAdminProfile")
    class CreateAdminProfile {

        @Test
        @DisplayName("성공")
        void createAdmin_success(){
            //given
            AdminProfile mockAdmin = mock(AdminProfile.class);
            when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
            when(accountProfileRepository.saveAdmin(account, email)).thenReturn(mockAdmin);

            //when
            boolean result = accountProfileService.createAdminProfile(accountId, email);

            //then
            assertThat(result).isTrue();
            verify(accountProfileRepository).saveAdmin(account, email);
        }
    }

    @Nested
    @DisplayName("조회 메소드")
    class FindFields {

        @Test
        void findEmail_success(){
            when(accountProfileRepository.findEmail(accountId)).thenReturn(Optional.of("test@email.com"));
            String email = accountProfileService.findEmail(accountId);
            assertThat(email).isEqualTo("test@email.com");
        }

        @Test
        void findRoleType_success(){
            when(accountProfileRepository.findRoleType(accountId)).thenReturn(Optional.of("ADMIN"));
            String role = accountProfileService.findRoleType(accountId);
            assertThat(role).isEqualTo("ADMIN");
        }

        @Test
        void findNickname_success(){
            when(accountProfileRepository.findNickname(accountId)).thenReturn(Optional.of("곰돌이"));
            String nickname = accountProfileService.findNickname(accountId);
            assertThat(nickname).isEqualTo("곰돌이");
        }

        @Test
        void findGender_success(){
            when(accountProfileRepository.findGender(accountId)).thenReturn(Optional.of("FEMALE"));
            String gender = accountProfileService.findGender(accountId);
            assertThat(gender).isEqualTo("FEMALE");
        }

        @Test
        void findBirthyear_success(){
            when(accountProfileRepository.findBirthyear(accountId)).thenReturn(Optional.of("1998"));
            String birthyear = accountProfileService.findBirthyear(accountId);
            assertThat(birthyear).isEqualTo("1998");
        }
    }
}
