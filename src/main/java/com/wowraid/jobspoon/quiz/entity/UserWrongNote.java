package com.wowraid.jobspoon.quiz.entity;

//import com.wowraid.jobspoon.account.entity.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "wrong_note")
public class UserWrongNote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 오답노트 ID

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "account_id", nullable = false)
//    private Account account; // 사용자 식별자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_question_id", nullable = false)
    private QuizQuestion quizQuestion; // 틀린 문제

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_choice_id", nullable = false)
    private QuizChoice quizChoice; // 사용자가 선택한 오답 보기와 해설

    private LocalDateTime submittedAt; // 오답 저장 시각
    private String explanation; // 해설

    public QuizQuestion getQuizQuestion() {
        return this.quizQuestion;
    }

    public UserWrongNote(QuizQuestion quizQuestion, QuizChoice quizChoice, LocalDateTime submittedAt, String explanation) {
        this.quizQuestion = quizQuestion;
        this.quizChoice = quizChoice;
        this.submittedAt = LocalDateTime.now();
        this.explanation = quizChoice.getExplanation();
    }

//    public UserWrongNote(Account account, QuizQuestion quizQuestion, QuizChoice quizChoice, LocalDateTime submittedAt, String explanation) {
//        this.account = account;
//        this.quizQuestion = quizQuestion;
//        this.quizChoice = quizChoice;
//        this.submittedAt = LocalDateTime.now();
//        this.explanation = quizChoice.getExplanation();
//    }
}
