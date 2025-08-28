package com.wowraid.jobspoon.quiz.entity;

import com.wowraid.jobspoon.quiz.entity.enums.QuestionType;
import com.wowraid.jobspoon.term.entity.Category;
import com.wowraid.jobspoon.term.entity.Term;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "quiz_question")
public class QuizQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "term_id", nullable = true)
    private Term term;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = true)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_set_id", nullable = true)
    private QuizSet quizSet;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false)
    private QuestionType questionType;

    @Setter
    private String questionText;

    @Setter
    private Integer questionAnswer;

    @Column(name = "is_random", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private boolean isRandom = false;

    @Transient
    private Integer orderIndex;

    public QuizQuestion(
            Term term,
            Category category,
            QuestionType questionType,
            String questionText,
            Integer questionAnswer
    ) {
        this(term, category, questionType, questionText, questionAnswer, null);
    }

    public QuizQuestion(Term term, Category category, QuestionType questionType, String questionText, Integer questionAnswer, QuizSet quizSet) {
        this.term = term;
        this.category = category;
        this.questionType = questionType;
        this.questionText = questionText;
        this.questionAnswer = questionAnswer;
        this.quizSet = quizSet;
    }

    public void setQuizSet(QuizSet quizSet) {
        this.quizSet = quizSet;
    }

}
