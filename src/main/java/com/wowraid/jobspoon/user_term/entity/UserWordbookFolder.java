package com.wowraid.jobspoon.user_term.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_wordbook_folder")
public class UserWordbookFolder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "account_id")
//    private Account account;

    @Column(name = "folder_name", nullable = false, length = 50)
    private String folderName;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.sortOrder == null) this.sortOrder = 0;
    }

    public UserWordbookFolder(String folderName, Integer sortOrder) {
        this.folderName = folderName;
        this.sortOrder = sortOrder;
    }

    public UserWordbookFolder(String folderName, Integer sortOrder, LocalDateTime createAt) {
        this.folderName = folderName;
        this.sortOrder = sortOrder;
        this.createdAt = createAt;
    }
}

