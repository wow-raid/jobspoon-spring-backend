package com.wowraid.jobspoon.quiz.entity;

import com.wowraid.jobspoon.quiz.entity.enums.QuestionType;
import com.wowraid.jobspoon.term.entity.Category;
import com.wowraid.jobspoon.term.entity.Term;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@NoArgsConstructor
@Table(
        name = "quiz_question",
        indexes = {
                @Index(name = "idx_quiz_question_set_order", columnList = "quiz_set_id, order_index"),
                @Index(name = "idx_quiz_question_category", columnList = "category_id"),
                @Index(name = "idx_quiz_question_term", columnList = "term_id")
        }
)
public class QuizQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 문제의 원천 용어 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "term_id", nullable = true)
    private Term term;

    /** 카테고리 기반 문제 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = true)
    private Category category;

    /** 소속 세트(FK) */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quiz_set_id", nullable = false)
    private QuizSet quizSet;

    /** 문제 유형 */
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false, columnDefinition = "enum('CHOICE', 'OX', 'INITIALS')")
    private QuestionType questionType;

    /** 문제 본문 */
    @Setter
    @Column(name = "question_text", nullable = false, length = 1000)
    private String questionText;

    /** 정답 인덱스/표현(유형에 따라 의미 다름) */
    @Setter
    @Column(name = "question_answer")
    private Integer questionAnswer;

    /** 생성 시 랜덤 로직 사용 여부 */
    @Setter
    @Column(name = "is_random", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private boolean isRandom = false;

    /** 세트 내 정렬용(영속) */
    @Setter
    @Column(name = "order_index")
    private Integer orderIndex;

    public QuizQuestion(
            Term term,
            Category category,
            QuestionType questionType,
            String questionText,
            Integer questionAnswer
    ) {
        this(term, category, questionType, questionText, questionAnswer, null, null);
    }

    public QuizQuestion(
            Term term,
            Category category,
            QuestionType questionType,
            String questionText,
            Integer questionAnswer,
            QuizSet quizSet
    ) {
        this(term, category, questionType, questionText, questionAnswer, quizSet, null);
    }

    public QuizQuestion(
            Term term,
            Category category,
            QuestionType questionType,
            String questionText,
            Integer questionAnswer,
            QuizSet quizSet,
            Integer orderIndex
    ) {
        this.term = term;
        this.category = category;
        this.questionType = questionType;
        this.questionText = questionText;
        this.questionAnswer = questionAnswer;
        this.quizSet = quizSet;
        this.orderIndex = orderIndex;
    }

    public void setQuizSet(QuizSet quizSet) {
        this.quizSet = quizSet;
    }
}
