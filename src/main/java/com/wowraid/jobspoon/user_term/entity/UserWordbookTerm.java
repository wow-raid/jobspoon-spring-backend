package com.wowraid.jobspoon.user_term.entity;

import com.wowraid.jobspoon.account.entity.Account;
import com.wowraid.jobspoon.term.entity.Term;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(
        name = "user_wordbook_term",
        uniqueConstraints = @UniqueConstraint(name="uk_owner_folder_term", columnNames = {"account_id","folder_id","term_id"}),
        indexes = {
                @Index(name="idx_uwt_folder", columnList="folder_id"),
                @Index(name="idx_uwt_term", columnList="term_id")
        }
)
@NoArgsConstructor
public class UserWordbookTerm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id", nullable = false)
    private UserWordbookFolder folder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "term_id", nullable = false)
    private Term term;
    
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0; // 정렬 순서

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public UserWordbookTerm(Account account, UserWordbookFolder folder, Term term) {
        this.account = account;
        this.folder = folder;
        this.term = term;
    }

    // 정렬 순서를 포함하는 생성자
    public UserWordbookTerm(Account account, UserWordbookFolder folder, Term term, Integer sortOrder) {
        this.account = account;
        this.folder = folder;
        this.term = term;
        this.sortOrder = (sortOrder == null ? 0 : sortOrder);
    }

    // 편의 팩토리: folder에서 account를 자동으로 가져옴
    public static UserWordbookTerm of(UserWordbookFolder folder, Term term, int sortOrder) {
        return new UserWordbookTerm(folder.getAccount(), folder, term, sortOrder);
    }

    // 세터 없이 저장 직전에 account 동기화(추가 안전망)
    @PrePersist
    @PreUpdate
    void syncAccountFromFolder() {
        if (this.folder != null) {
            this.account = this.folder.getAccount();
        }
    }
}
