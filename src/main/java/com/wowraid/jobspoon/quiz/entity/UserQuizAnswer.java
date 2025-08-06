package com.wowraid.jobspoon.quiz.entity;

import com.wowraid.jobspoon.account.entity.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_quiz_answer")
public class UserQuizAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private QuizQuestion question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_choice_id", nullable = false)
    private QuizChoice quizChoice;

    private LocalDateTime submittedAt;

    private boolean isCorrect;

    public UserQuizAnswer(Account account, QuizQuestion question, QuizChoice quizChoice) {
        this.account = account;
        this.question = question;
        this.quizChoice = quizChoice;
        this.submittedAt = LocalDateTime.now();
        this.isCorrect = quizChoice.isAnswer();
    }
}
