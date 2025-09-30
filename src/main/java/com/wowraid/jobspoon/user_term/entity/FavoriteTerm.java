package com.wowraid.jobspoon.user_term.entity;

//import com.wowraid.jobspoon.account.entity.Account;
import com.wowraid.jobspoon.account.entity.Account;
import com.wowraid.jobspoon.term.entity.Term;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "favorite_term")
public class FavoriteTerm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "term_id")
    private Term term;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id", nullable = false)
    private UserWordbookFolder folder;

    private LocalDateTime createdAt;
    // 생성 시 자동으로 createdAt 설정
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public FavoriteTerm(Term term) {
        this.term = term;
        this.createdAt = LocalDateTime.now();
    }
}
