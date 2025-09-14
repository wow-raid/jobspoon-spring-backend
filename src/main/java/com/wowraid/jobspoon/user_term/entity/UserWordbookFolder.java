package com.wowraid.jobspoon.user_term.entity;

import com.wowraid.jobspoon.account.entity.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Locale;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "user_wordbook_folder",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_folder_owner_normalized",
                columnNames = {"account_id", "normalized_folder_name"}
        )
)
public class UserWordbookFolder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "folder_name", nullable = false, length = 50)
    private String folderName;

    @Column(name = "normalized_folder_name", nullable = false, length = 50)
    private String normalizedFolderName;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
        if (this.sortOrder == null) this.sortOrder = 0;
        ensureNormalized();
    }

    @PreUpdate
    protected void onUpdate() {
        ensureNormalized();
    }

    private void ensureNormalized() {
        if (this.folderName != null) {
            this.normalizedFolderName = normalize(this.folderName);
        }
    }

    private static String normalize(String s) {
        return s.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }

    // 기존 생성자들은 그대로 두되, 새 생성자에도 normalized 세팅 옵션 유지
    public UserWordbookFolder(Account account, String folderName, Integer sortOrder) {
        this.account = account;
        this.folderName = folderName;
        this.sortOrder = sortOrder;
    }

    public UserWordbookFolder(Account account, String folderName, Integer sortOrder, String normalized) {
        this.account = account;
        this.folderName = folderName;
        this.normalizedFolderName = normalized;
        this.sortOrder = sortOrder;
    }
}
