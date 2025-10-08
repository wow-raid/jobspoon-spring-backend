package com.wowraid.jobspoon.userTitle.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "title")
public class UserTitle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long accountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "title_code", nullable = false)
    private TitleCode titleCode;

    private LocalDateTime acquiredAt;

    @Column(nullable = false)
    private boolean isEquipped;

    public void setEquipped(boolean equipped) {
        this.isEquipped = equipped;
    }
}