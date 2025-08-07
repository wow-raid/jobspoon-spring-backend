package com.wowraid.jobspoon.account.repository;

import com.wowraid.jobspoon.account.entity.Account;
import com.wowraid.jobspoon.account.entity.AccountLoginType;
import com.wowraid.jobspoon.account.entity.AccountRoleType;
import com.wowraid.jobspoon.account.entity.WithdrawalMembership;
import com.wowraid.jobspoon.account.entity.enums.LoginType;
import com.wowraid.jobspoon.account.entity.enums.RoleType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@Transactional
public class AccountCustomRepositoryImpl implements AccountCustomRepository {

    @PersistenceContext
    private EntityManager em;

    private final WithdrawalMembershipRepository withdrawalMembershipRepository;

    public AccountCustomRepositoryImpl(WithdrawalMembershipRepository withdrawalMembershipRepository) {
        this.withdrawalMembershipRepository = withdrawalMembershipRepository;
    }

    @Override
    public void save(String email, String loginTypeStr) {
        saveWithRole(email, loginTypeStr, RoleType.NORMAL);
    }

    @Override
    public void saveAdmin(String email, String loginTypeStr) {
        saveWithRole(email, loginTypeStr, RoleType.ADMIN);
    }

    private void saveWithRole(String email, String loginTypeStr, RoleType roleTypeEnum) {
        LoginType loginType = LoginType.valueOf(loginTypeStr);

        AccountRoleType roleType = findOrCreateRoleType(roleTypeEnum);
        AccountLoginType loginTypeEntity = findOrCreateLoginType(loginType);

        Account account = new Account(email, roleType, loginTypeEntity);
        em.persist(account);
    }

    private AccountRoleType findOrCreateRoleType(RoleType roleTypeEnum) {
        return em.createQuery(
                        "SELECT r FROM AccountRoleType r WHERE r.roleEnum = :role", AccountRoleType.class)
                .setParameter("role", roleTypeEnum)
                .getResultStream()
                .findFirst()
                .orElseGet(() -> {
                    AccountRoleType newRole = new AccountRoleType(roleTypeEnum);
                    em.persist(newRole);
                    return newRole;
                });
    }

    private AccountLoginType findOrCreateLoginType(LoginType loginType) {
        return em.createQuery(
                        "SELECT l FROM AccountLoginType l WHERE l.loginType = :login", AccountLoginType.class)
                .setParameter("login", loginType)
                .getResultStream()
                .findFirst()
                .orElseGet(() -> {
                    AccountLoginType newLogin = new AccountLoginType(loginType);
                    em.persist(newLogin);
                    return newLogin;
                });
    }

    @Override
    public boolean deleteAccount(String accountIdStr) {
        try {
            Long accountId = Long.parseLong(accountIdStr);
            Account account = em.find(Account.class, accountId);
            if (account != null) {
                em.remove(account);
                return true;
            }
        } catch (NumberFormatException e) {
            System.err.println("Invalid accountId: " + accountIdStr);
        }
        return false;
    }


    // 1. 탈퇴 정보 저장
    public WithdrawalMembership saveWithdrawInfo(String accountId) {
        WithdrawalMembership withdrawal = new WithdrawalMembership(
                accountId, null, null
        );
        em.persist(withdrawal);
        return withdrawal;
    }

    // 2. 탈퇴 시각 저장
    public void saveWithdrawAt(String accountId, LocalDateTime time) {
        Optional<WithdrawalMembership> optional = withdrawalMembershipRepository.findByAccountId(accountId);
        optional.ifPresent(wm -> {
            wm.setWithdrawAt(time);
            withdrawalMembershipRepository.save(wm);
        });
    }

    // 3. 탈퇴 만료일 저장
    public void saveWithdrawEnd(String accountId, LocalDateTime time) {
        Optional<WithdrawalMembership> optional = withdrawalMembershipRepository.findByAccountId(accountId);
        optional.ifPresent(wm -> {
            wm.setWithdrawEnd(time.plusYears(3));
            withdrawalMembershipRepository.save(wm);
        });
    }
}
