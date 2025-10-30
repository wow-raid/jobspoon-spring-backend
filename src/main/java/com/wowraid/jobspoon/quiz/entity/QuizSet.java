package com.wowraid.jobspoon.quiz.entity;

import com.wowraid.jobspoon.quiz.entity.enums.QuizPartType;
import com.wowraid.jobspoon.term.entity.Category;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "quiz_set")
public class QuizSet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private boolean isRandom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = true)
    @ToString.Exclude
    private Category category;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "part_type", length = 20, nullable = false)
    private QuizPartType partType;

    public QuizPartType getPartType() { return partType; }

    public QuizSet(String title, boolean isRandom) {
        this.title = title;
        this.isRandom = isRandom;
    }

    public QuizSet(String title, Category category, boolean isRandom) {
        this.title = title;
        this.category = category;
        this.isRandom = isRandom;
    }
}
