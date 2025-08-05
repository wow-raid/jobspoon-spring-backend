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

    // 응답한 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    // 어떤 문제에 대한 응답인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private QuizQuestion question;

    // 사용자가 선택한 보기 (ManyToOne 연관관계)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_choice_id", nullable = false)
    private QuizChoice quizChoice;

    // 응답 시각
    private LocalDateTime submittedAt;

    // 정답 여부(QuizChoice.isAnswer 기준 자동 판별)
    private boolean isCorrect;

    public UserQuizAnswer(Account account, QuizQuestion question, QuizChoice quizChoice) {
        this.account = account;
        this.question = question;
        this.quizChoice = quizChoice;
        this.submittedAt = LocalDateTime.now();
        this.isCorrect = quizChoice.isAnswer();
    }
}
