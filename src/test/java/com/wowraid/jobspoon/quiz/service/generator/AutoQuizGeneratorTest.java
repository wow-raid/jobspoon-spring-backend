package com.wowraid.jobspoon.quiz.service.generator;

import com.wowraid.jobspoon.quiz.entity.QuizQuestion;
import com.wowraid.jobspoon.quiz.entity.enums.QuestionType;
import com.wowraid.jobspoon.quiz.entity.enums.SeedMode;
import com.wowraid.jobspoon.quiz.repository.QuizChoiceRepository;
import com.wowraid.jobspoon.term.entity.Term;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class AutoQuizGeneratorTest {

    private final QuizChoiceRepository quizChoiceRepository = Mockito.mock(QuizChoiceRepository.class);
    private final AutoQuizGenerator generator = new AutoQuizGenerator(quizChoiceRepository);

    @Test
    void generateQuestions_dailySeed_reproducible() {
        var terms = fakeTerms(10);
        var types = List.of(QuestionType.CHOICE);

        var r1 = generator.generateQuestions(terms, types, 5, null, null, null,
                SeedMode.DAILY, 100L, null, "MEDIUM");
        var r2 = generator.generateQuestions(terms, types, 5, null, null, null,
                SeedMode.DAILY, 100L, null, "MEDIUM");

        assertThat(r1.stream().map(QuizQuestion::getQuestionText).toList())
                .isEqualTo(r2.stream().map(QuizQuestion::getQuestionText).toList());
    }

    @Test
    void fixed_sameFixedSeed_sameOrder() {
        var gen = new AutoQuizGenerator(quizChoiceRepository);
        var terms = fakeTerms(10);
        var a = gen.generateQuestions(terms, List.of(QuestionType.CHOICE), 5, null, null, null,
                SeedMode.FIXED, 1L, 777L, "MEDIUM");
        var b = gen.generateQuestions(terms, List.of(QuestionType.CHOICE), 5, null, null, null,
                SeedMode.FIXED, 999L, 777L, "MEDIUM");
        assertEquals(ids(a), ids(b));
    }

    @Test
    void auto_usuallyDifferent() {
        var gen = new AutoQuizGenerator(quizChoiceRepository);
        var terms = fakeTerms(10);
        var a = gen.generateQuestions(terms, List.of(QuestionType.CHOICE), 5, null, null, null,
                SeedMode.AUTO, 1L, null, "MEDIUM");
        var b = gen.generateQuestions(terms, List.of(QuestionType.CHOICE), 5, null, null, null,
                SeedMode.AUTO, 1L, null, "MEDIUM");
        assertNotEquals(ids(a), ids(b));
    }

    private List<Long> ids(List<QuizQuestion> qs){ return qs.stream().map(q -> q.getTerm().getId()).toList(); }

    private List<Term> fakeTerms(int n) {
        List<Term> list = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            Term t = new Term(); // 필요 시 실제 생성자/빌더 사용
            setField(t, "id", (long) i);
            setField(t, "title", "용어" + i);
            setField(t, "description", "용어" + i + "에 대한 설명이다");
            list.add(t);
        }
        return list;
    }

    private static void setField(Object target, String name, Object value) {
        try {
            Field f = target.getClass().getDeclaredField(name);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
