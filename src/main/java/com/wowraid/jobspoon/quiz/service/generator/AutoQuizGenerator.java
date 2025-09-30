package com.wowraid.jobspoon.quiz.service.generator; // ← 패키지 선언 꼭!

import com.wowraid.jobspoon.quiz.entity.QuizChoice;
import com.wowraid.jobspoon.quiz.entity.QuizQuestion;
import com.wowraid.jobspoon.quiz.entity.enums.QuestionType;
import com.wowraid.jobspoon.quiz.repository.QuizChoiceRepository;
import com.wowraid.jobspoon.term.entity.Term;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class AutoQuizGenerator {

    private final QuizChoiceRepository quizChoiceRepository;

    /** 1) 문항만 생성 (저장/보기 저장 안 함) */
    public List<QuizQuestion> generateQuestions(
            List<Term> terms, List<QuestionType> types, Integer count,
            Integer mcqEach, Integer oxEach, Integer initialsEach,
            String seedMode, String difficulty
    ) {
        List<Term> pool = shuffleBySeed(terms, seedMode);
        int target = (count != null && count > 0) ? count : calcByEach(pool, mcqEach, oxEach, initialsEach);

        List<QuizQuestion> out = new ArrayList<>();
        for (Term t : pool) {
            for (QuestionType type : types) {
                if (out.size() >= target) break;
                QuizQuestion q = switch (type) {
                    case CHOICE -> mkChoice(t);
                    case OX -> mkOX(t);
                    case INITIALS -> mkInitials(t);
                };
                out.add(q);
            }
            if (out.size() >= target) break;
        }
        return out;
    }

    /** 2) 저장된 문항들에 대해 보기 생성·저장 (이때는 q가 Managed) */
    public void createAndSaveChoicesFor(List<QuizQuestion> questions) {
        for (QuizQuestion q : questions) {
            if (q.getQuestionType() == QuestionType.OX) {
                var choices = List.of(
                        new QuizChoice(q, "O", true,  "정답 해설"),
                        new QuizChoice(q, "X", false, "오답 해설")
                );
                quizChoiceRepository.saveAll(choices);
                q.setQuestionAnswer(1);
                continue;
            }

            String correct = (q.getQuestionType() == QuestionType.INITIALS)
                    ? toChoseong(q.getTerm().getTitle())
                    : normalize(q.getTerm().getDescription());

            List<String> distractors = pickDistractors(q.getTerm(), q.getQuestionType());
            List<String> options = new ArrayList<>();
            options.add(correct);
            options.addAll(distractors.stream().limit(3).toList());
            Collections.shuffle(options);

            int answerIdx = -1;
            for (int i = 0; i < options.size(); i++) {
                boolean isAns = options.get(i).equals(correct);
                if (isAns) answerIdx = i + 1;
                quizChoiceRepository.save(new QuizChoice(q, options.get(i), isAns, isAns ? "정답 해설" : "오답 해설"));
            }
            q.setQuestionAnswer(answerIdx);
        }
    }

    // --- 문항 템플릿 ---
    private QuizQuestion mkChoice(Term t) {
        String stem = normalize(t.getDescription());
        return new QuizQuestion(t, t.getCategory(), QuestionType.CHOICE, stem, /*answerIdx*/ 1);
    }
    private QuizQuestion mkOX(Term t) {
        String stem = "다음 설명은 '" + t.getTitle() + "'에 대한 올바른 설명이다.";
        return new QuizQuestion(t, t.getCategory(), QuestionType.OX, stem, /*answerIdx*/ 1);
    }
    private QuizQuestion mkInitials(Term t) {
        String stem = normalize(t.getDescription());
        return new QuizQuestion(t, t.getCategory(), QuestionType.INITIALS, stem, /*answerIdx*/ 1);
    }

    // --- 유틸(스텁) ---
    private List<Term> shuffleBySeed(List<Term> terms, String seedMode) { return terms; }
    private int calcByEach(List<Term> pool, Integer mcqEach, Integer oxEach, Integer initialsEach) {
        int e = (mcqEach == null ? 0 : mcqEach) + (oxEach == null ? 0 : oxEach) + (initialsEach == null ? 0 : initialsEach);
        return Math.max(1, e * pool.size());
    }
    private String normalize(String s){ return (s == null || s.isBlank()) ? "" : (s.endsWith("다")? s : s + "이다."); }
    private String toChoseong(String title){ return "ㄷㅇㅁ"; } // TODO: 실제 구현
    private List<String> pickDistractors(Term t, QuestionType type){ return List.of("오답1","오답2","오답3","오답4"); }
}
