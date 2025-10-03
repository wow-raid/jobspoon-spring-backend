package com.wowraid.jobspoon.quiz.service.generator;

import com.wowraid.jobspoon.quiz.entity.QuizChoice;
import com.wowraid.jobspoon.quiz.entity.QuizQuestion;
import com.wowraid.jobspoon.quiz.entity.enums.QuestionType;
import com.wowraid.jobspoon.quiz.entity.enums.SeedMode;
import com.wowraid.jobspoon.quiz.repository.QuizChoiceRepository;
import com.wowraid.jobspoon.quiz.service.util.SeedUtil;
import com.wowraid.jobspoon.term.entity.Term;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AutoQuizGenerator {

    private final QuizChoiceRepository quizChoiceRepository;
    private final DifficultyProperties difficultyProperties;

    private final SeedUtil seedUtil = new SeedUtil();

    /** 1) 문항만 생성 (저장/보기 저장 안 함) */
    public List<QuizQuestion> generateQuestions(
            List<Term> terms,
            List<QuestionType> types,
            Integer count,
            Integer mcqEach,
            Integer oxEach,
            Integer initialsEach,
            SeedMode seedMode,
            Long accountId,         // DAILY 재현성용
            Long fixedSeed,         // FIXED 재현성용
            String difficulty
    ) {
        long seed = seedUtil.resolveSeed(seedMode, accountId, fixedSeed);
        Random rng = new Random(seed);

        DifficultyProperties.Profile profile = difficultyProperties.getProfileOrDefault(difficulty);

        // 시드 기반 셔플
        List<Term> pool = shuffleBySeed(terms, rng);
        int target = (count != null && count > 0) ? count : calcByEach(pool, mcqEach, oxEach, initialsEach);

        List<QuizQuestion> out = new ArrayList<>();
        for (Term t : pool) {
            for (QuestionType type : types) {
                if (out.size() >= target) break;
                QuizQuestion q = switch (type) {
                    case CHOICE   -> mkChoice(t, profile, rng);
                    case OX       -> mkOX(t, profile, rng);
                    case INITIALS -> mkInitials(t, profile);
                };
                out.add(q);
            }
            if (out.size() >= target) break;
        }
        return out;
    }

    /** 2) 저장된 문항들에 대해 보기 생성·저장 (이때는 q가 Managed) */
    public void createAndSaveChoicesFor(List<QuizQuestion> questions,
                                        SeedMode seedMode,
                                        Long accountId,
                                        Long fixedSeed) {
        long seed = seedUtil.resolveSeed(seedMode, accountId, fixedSeed);
        Random rng = new Random(seed);

        for (QuizQuestion q : questions) {
            // 난이도는 question에 따로 저장되어 있지 않으므로, 일단 MEDIUM 가정(혹은 stem 내 메타 태그에서 파싱하도록 확장 가능)
            DifficultyProperties.Profile profile = difficultyProperties.getProfileOrDefault("MEDIUM");

            if (q.getQuestionType() == QuestionType.OX) {
                // OX: 변형된 지문이라도 보기 자체는 O/X 고정
                var choices = List.of(
                        new QuizChoice(q, "O", true,  "정답 해설"),
                        new QuizChoice(q, "X", false, "오답 해설")
                );
                quizChoiceRepository.saveAll(choices);
                q.setQuestionAnswer(1); // O 인덱스 고정
                continue;
            }

            // 정답 텍스트
            String correct = (q.getQuestionType() == QuestionType.INITIALS)
                    ? toChoseong(q.getTerm().getTitle())
                    : normalize(q.getTerm().getDescription());

            // 오답 후보 생성(난이도 기반 유사도/개수 적용)
            List<String> distractors = pickDistractors(q.getTerm(), q.getQuestionType(), profile, rng);

            // 보기 구성
            int optionCount = Math.max(2, profile.getOptionCount());
            List<String> options = new ArrayList<>(optionCount);
            options.add(correct);
            options.addAll(distractors.stream().limit(optionCount - 1).toList());

            // 길이 편향 완화: 옵션 길이 분산이 너무 크면 재셔플 한번 더
            Collections.shuffle(options, rng);
            if (!acceptLengthBias(options, profile.getLengthBiasTolerance())) {
                Collections.shuffle(options, rng);
            }

            // 저장 & 정답 인덱스 기록(1-based)
            int answerIdx = -1;
            for (int i = 0; i < options.size(); i++) {
                boolean isAns = options.get(i).equals(correct);
                if (isAns) answerIdx = i + 1;
                quizChoiceRepository.save(new QuizChoice(
                        q, options.get(i), isAns, isAns ? "정답 해설" : "오답 해설"));
            }
            q.setQuestionAnswer(answerIdx);
        }
    }

    // --- 문항 템플릿(+난이도 지문 변형) ---
    private QuizQuestion mkChoice(Term t, DifficultyProperties.Profile p, Random rng) {
        String stem = normalize(t.getDescription());
        stem = maybeNegateOrReplace(stem, p, rng);
        return new QuizQuestion(t, t.getCategory(), QuestionType.CHOICE, stem, /*answerIdx*/ 1);
    }

    private QuizQuestion mkOX(Term t, DifficultyProperties.Profile p, Random rng) {
        String base = "다음 설명은 '" + t.getTitle() + "'에 대한 올바른 설명이다.";
        base = maybeNegateOrReplace(base, p, rng);
        return new QuizQuestion(t, t.getCategory(), QuestionType.OX, base, /*answerIdx*/ 1);
    }

    private QuizQuestion mkInitials(Term t, DifficultyProperties.Profile p) {
        String desc = normalize(t.getDescription());
        // 난이도별 힌트(글자수, 일부 초성 공개)
        StringBuilder stem = new StringBuilder(desc);
        if (p.isRevealLength()) {
            stem.append(" (글자수: ").append(lengthNoSpaces(t.getTitle())).append(")");
        }
        if (p.getRevealInitialsCount() > 0) {
            String initials = toChoseong(t.getTitle());
            int n = Math.min(p.getRevealInitialsCount(), initials.length());
            stem.append(" (초성 힌트: ").append(initials.substring(0, n)).append("…)");
        }
        return new QuizQuestion(t, t.getCategory(), QuestionType.INITIALS, stem.toString(), /*answerIdx*/ 1);
    }

    // --- 시드/셔플 유틸 ---
    private List<Term> shuffleBySeed(List<Term> terms, Random rng) {
        if (terms == null || terms.size() <= 1) return terms;
        List<Term> copy = new ArrayList<>(terms);
        Collections.shuffle(copy, rng);
        return copy;
    }

    private int calcByEach(List<Term> pool, Integer mcqEach, Integer oxEach, Integer initialsEach) {
        int e = (mcqEach == null ? 0 : mcqEach) + (oxEach == null ? 0 : oxEach) + (initialsEach == null ? 0 : initialsEach);
        return Math.max(1, e * pool.size());
    }

    // --- 텍스트/유사도/오답 선택 ---
    private String normalize(String s) {
        return (s == null || s.isBlank()) ? "" : (s.endsWith("다") ? s : s + "이다.");
    }

    private String maybeNegateOrReplace(String stem, DifficultyProperties.Profile p, Random rng) {
        String out = stem;
        if (rng.nextDouble() < p.getNegateProb()) {
            // 아주 단순한 부정화(예시): "올바른" → "올바르지 않은"
            out = out.replace("올바른", "올바르지 않은")
                    .replace("맞는", "맞지 않은");
        }
        if (rng.nextDouble() < p.getReplaceProb()) {
            // 단순 치환(예시): "다음 설명은" → "아래 진술은"
            out = out.replace("다음 설명은", "아래 진술은");
        }
        return out;
    }

    private int lengthNoSpaces(String s) {
        return (s == null) ? 0 : s.replace(" ", "").length();
    }

    private String toChoseong(String title) {
        // TODO: 실제 초성 변환 구현
        return "ㄷㅇㅁ";
    }

    private List<String> pickDistractors(Term t, QuestionType type, DifficultyProperties.Profile p, Random rng) {
        // TODO: 실제 구현 — 같은 카테고리/태그에서 후보 수집
        // 일단 플레이스홀더 풀에서 유사도/개수 정책만 적용
        List<String> rawPool = List.of("오답1", "오답2", "오답3", "오답4", "오답5", "오답6", "오답7", "오답8");

        String answer = (type == QuestionType.INITIALS)
                ? toChoseong(t.getTitle())
                : normalize(t.getDescription());

        // 유사도 필터링(Jaccard on token set)
        List<String> filtered = rawPool.stream()
                .filter(s -> {
                    double sim = jaccard(tokenize(answer), tokenize(s));
                    return sim >= p.getSimilarityMin() && sim <= p.getSimilarityMax();
                })
                .collect(Collectors.toCollection(ArrayList::new));

        // 부족하면 채우기(유사도 무시, 재현성 있게)
        if (filtered.size() < p.getMaxDistractors()) {
            for (String s : rawPool) {
                if (filtered.size() >= p.getMaxDistractors()) break;
                if (!filtered.contains(s)) filtered.add(s);
            }
        }

        // 시드 기반 셔플 후 리미트
        Collections.shuffle(filtered, rng);
        return filtered.stream().limit(p.getMaxDistractors()).toList();
    }

    private Set<String> tokenize(String s) {
        if (s == null) return Set.of();
        String[] arr = s.replaceAll("[^ㄱ-ㅎ가-힣a-zA-Z0-9 ]", " ")
                .toLowerCase(Locale.ROOT)
                .split("\\s+");
        return Arrays.stream(arr).filter(w -> !w.isBlank()).collect(Collectors.toSet());
    }

    private double jaccard(Set<String> a, Set<String> b) {
        if (a.isEmpty() && b.isEmpty()) return 1.0;
        Set<String> inter = new HashSet<>(a);
        inter.retainAll(b);
        Set<String> union = new HashSet<>(a);
        union.addAll(b);
        return union.isEmpty() ? 0.0 : (double) inter.size() / union.size();
    }

    private boolean acceptLengthBias(List<String> options, double tolerance) {
        if (options.isEmpty()) return true;
        int min = options.stream().mapToInt(String::length).min().orElse(0);
        int max = options.stream().mapToInt(String::length).max().orElse(0);
        if (max == 0) return true;
        double diffRatio = (double) (max - min) / max;
        return diffRatio <= Math.max(0, Math.min(1, tolerance));
    }
}