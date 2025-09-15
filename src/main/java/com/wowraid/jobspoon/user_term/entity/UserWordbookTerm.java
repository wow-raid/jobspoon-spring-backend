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

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public UserWordbookTerm(Account account, UserWordbookFolder folder, Term term) {
        this.account = account;
        this.folder = folder;
        this.term = term;
    }
}
