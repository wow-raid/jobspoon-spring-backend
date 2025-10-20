package com.wowraid.jobspoon.quiz.service;

import com.wowraid.jobspoon.quiz.entity.QuizChoice;
import com.wowraid.jobspoon.quiz.entity.QuizQuestion;
import com.wowraid.jobspoon.quiz.entity.QuizSet;
import com.wowraid.jobspoon.quiz.entity.enums.QuestionType;
import com.wowraid.jobspoon.quiz.entity.enums.SeedMode;
import com.wowraid.jobspoon.quiz.repository.QuizChoiceRepository;
import com.wowraid.jobspoon.quiz.repository.QuizQuestionRepository;
import com.wowraid.jobspoon.quiz.repository.QuizSetRepository;
import com.wowraid.jobspoon.quiz.repository.SessionAnswerRepository;
import com.wowraid.jobspoon.quiz.service.generator.AutoQuizGenerator;
import com.wowraid.jobspoon.quiz.service.request.CreateQuizSessionRequest;
import com.wowraid.jobspoon.quiz.service.request.CreateQuizSetByCategoryRequest;
import com.wowraid.jobspoon.quiz.service.request.CreateQuizSetByFolderRequest;
import com.wowraid.jobspoon.quiz.service.response.BuiltQuizSetResponse;
import com.wowraid.jobspoon.quiz.service.response.CreateQuizSessionResponse;
import com.wowraid.jobspoon.quiz.service.response.CreateQuizSetByCategoryResponse;
import com.wowraid.jobspoon.term.entity.Category;
import com.wowraid.jobspoon.term.entity.Term;
import com.wowraid.jobspoon.term.repository.CategoryRepository;
import com.wowraid.jobspoon.user_term.repository.FavoriteTermRepository;
import com.wowraid.jobspoon.user_term.repository.UserWordbookTermRepository;
import com.wowraid.jobspoon.user_term.service.UserWordbookFolderQueryService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuizSetServiceImpl implements QuizSetService {

    private final CategoryRepository categoryRepository;
    private final QuizSetRepository quizSetRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final FavoriteTermRepository favoriteTermRepository;
    private final AutoQuizGenerator autoQuizGenerator;
    private final UserWordbookFolderQueryService userWordbookFolderQueryService;
    private final SessionAnswerRepository sessionAnswerRepository;
    private final QuizChoiceRepository quizChoiceRepository;

    /** 선택 주입: 있으면 사용(최근 옵션 텍스트 재사용 회피 등), 없으면 SessionAnswerRepository로 폴백 */
    @Autowired(required = false)
    private RecentUsageService recentUsageService;

    @PersistenceContext
    private EntityManager em;
    @Autowired
    private UserWordbookTermRepository userWordbookTermRepository;

    /**
     * 세트 생성 + 문항 ID까지 응답(프론트에서 session 시작용으로 쓰기 좋음)
     */
    @Override
    @Transactional
    public BuiltQuizSetResponse registerQuizSetByCategoryReturningQuestions(CreateQuizSetByCategoryRequest request) {

        // 1) 카테고리 로드(있는 경우)
        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.getReferenceById(request.getCategoryId());
        }

        // 2) 타이틀 확정
        String title = request.getTitle();
        if (title == null || title.isBlank()) {
            String cat = (category != null && category.getName() != null) ? category.getName() : "카테고리";
            String ts  = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            title = cat + " 퀴즈 - " + ts;
        }

        // 3) 세트 생성/저장 (PK 확보)
        QuizSet quizSet = new QuizSet(title, category, request.isRandom());
        quizSetRepository.save(quizSet);

        // 4) 문제로 사용할 용어 선별
        List<Term> pickedTerms = pickTermsByCategoryPolicy(
                request.getCategoryId(),
                request.getCount(),
                request.isRandom()
        );

        // 5) QuizQuestion 생성/저장
        List<QuizQuestion> questions = new ArrayList<>(pickedTerms.size());
        int order = 0;
        for (Term term : pickedTerms) {
            QuestionType qType = resolveQuestionType(request.getQuestionType(), term);

            QuizQuestion q;
            if (qType == QuestionType.INITIALS) {
                // INITIALS는 텍스트 정답(초성) 사용
                q = QuizQuestion.textAnswer(
                        term,
                        category,
                        QuestionType.INITIALS,
                        makeQuestionText(term, qType),
                        toKoreanInitials(term.getTitle()),
                        quizSet,
                        ++order
                );
            } else {
                // CHOICE/OX: 정답 인덱스는 보기 생성 후 확정
                q = new QuizQuestion(
                        term,
                        category,
                        qType,
                        makeQuestionText(term, qType),
                        /* answerIndex */ null,
                        quizSet
                );
                q.setOrderIndex(++order);
            }
            q.setRandom(request.isRandom());
            questions.add(q);
        }
        quizQuestionRepository.saveAll(questions);

        // 6) 보기 생성 (카테고리 풀 기반)
        List<Term> pool = buildPoolForCategory(request.getCategoryId(), pickedTerms);
        createChoicesForQuestions(questions, pool);

        // 7) 질문 ID 조회(id ASC)
        List<Long> questionIds = em.createQuery(
                "select q.id from QuizQuestion q where q.quizSet.id = :sid order by q.id",
                Long.class
        ).setParameter("sid", quizSet.getId()).getResultList();

        // 8) 결과 반환
        return BuiltQuizSetResponse.builder()
                .quizSetId(quizSet.getId())
                .questionIds(questionIds)
                .title(quizSet.getTitle())
                .isRandom(quizSet.isRandom())
                .totalQuestions(questionIds.size())
                .build();
    }

    @Override
    @Transactional
    public BuiltQuizSetResponse registerQuizSetByFolderReturningQuestions(CreateQuizSetByFolderRequest request) {

        // 0) 입력 검증
        if (request == null || request.getAccountId() == null || request.getFolderId() == null) {
            throw new IllegalArgumentException("계정 또는 폴더 식별자가 없습니다.");
        }
        if (request.getCount() <= 0) {
            throw new IllegalArgumentException("문항 수가 올바르지 않습니다.");
        }

        // 1) 폴더 소유권 확인
        boolean owned = userWordbookFolderQueryService.existsByIdAndAccountId(
                request.getFolderId(), request.getAccountId()
        );
        if (!owned) {
            throw new SecurityException("폴더가 없거나 권한이 없습니다.");
        }

        // 2) 폴더 용어 조회
        List<Long> candidateTermIds =
                userWordbookTermRepository.findDistinctTermIdsByFolderAndAccountOrderByTermIdAsc(
                        request.getFolderId(), request.getAccountId());

        if (candidateTermIds.isEmpty()) {
            // 폴더가 비었을 때만 400
            throw new IllegalArgumentException("폴더 내에 학습할 용어가 없습니다.");
        }

        // 요청 개수와 보유 개수 중 더 작은 값으로 클램프
        int targetCount = Math.min(Math.max(1, request.getCount()), candidateTermIds.size());

        // 3) 타이틀 확정
        String title = request.getTitle();
        if (title == null || title.isBlank()) {
            String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            title = "[SpoonNote] Folder#" + request.getFolderId() + " - " + ts;
        }

        // 4) 세트 저장
        QuizSet set = quizSetRepository.save(new QuizSet(title, null, request.isRandom()));

        // 5) Term 엔티티 로드 + 폴더 내 순서 보존
        List<Term> loaded = em.createQuery(
                        "select t from Term t where t.id in :ids", Term.class)
                .setParameter("ids", candidateTermIds)
                .getResultList();

        Map<Long, Term> termById = loaded.stream()
                .collect(Collectors.toMap(Term::getId, t -> t));

        // 폴더에서 꺼낸 id 순서대로 풀 구성
        List<Term> pool = candidateTermIds.stream()
                .map(termById::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (request.isRandom()) {
            Collections.shuffle(pool);
        }

        List<Term> picked = pool.subList(0, targetCount);

        // 6) 문항 생성/저장
        List<QuizQuestion> questions = new ArrayList<>(picked.size());
        int order = 0;
        for (Term term : picked) {
            // 기본은 MIX 분배
            QuestionType questionType = mixByTerm(term);

            var reqType = request.getQuestionType(); // DTO enum (nullable 가능)
            if (reqType != null) {
                switch (reqType) {
                    case CHOICE   -> questionType = QuestionType.CHOICE;
                    case OX       -> questionType = QuestionType.OX;
                    case INITIALS -> questionType = QuestionType.INITIALS;
                    case MIX      -> questionType = mixByTerm(term);
                    default       -> questionType = mixByTerm(term);
                }
            }

            QuizQuestion q;
            if (questionType == QuestionType.INITIALS) {
                // INITIALS는 텍스트 정답(초성)
                q = QuizQuestion.textAnswer(
                        term,
                        /* category */ null,
                        QuestionType.INITIALS,
                        makeQuestionText(term, questionType),
                        toKoreanInitials(term.getTitle()),
                        set,
                        ++order
                );
            } else {
                // CHOICE/OX: 정답 인덱스는 보기 생성 후 확정
                q = new QuizQuestion(
                        term,
                        /* category */ null,
                        questionType,
                        makeQuestionText(term, questionType),
                        /* answerIndex */ null,
                        set
                );
                q.setOrderIndex(++order);
            }
            q.setRandom(request.isRandom());
            questions.add(q);
        }
        quizQuestionRepository.saveAll(questions);

        // 7) questionIds 조회(id ASC)
        List<Long> questionIds = em.createQuery(
                "select q.id from QuizQuestion q where q.quizSet.id = :sid order by q.id",
                Long.class
        ).setParameter("sid", set.getId()).getResultList();

        // 8) 응답
        return BuiltQuizSetResponse.builder()
                .quizSetId(set.getId())
                .questionIds(questionIds)
                .title(set.getTitle())
                .isRandom(set.isRandom())
                .totalQuestions(questionIds.size())   // ← 실제 생성 개수(7) 반환
                .build();
    }

    /**
     * (세트 전용 응답) 필요한 경우 위 메서드를 호출해 DTO 변환만 수행
     */
    @Override
    @Transactional
    public CreateQuizSetByCategoryResponse registerQuizSetByCategory(CreateQuizSetByCategoryRequest request) {
        BuiltQuizSetResponse built = registerQuizSetByCategoryReturningQuestions(request);
        QuizSet set = quizSetRepository.getReferenceById(built.getQuizSetId());
        return CreateQuizSetByCategoryResponse.from(set, built.getQuestionIds());
    }

    /**
     * 즐겨찾기 기반 세션 생성
     */
    @Override
    @Transactional
    public CreateQuizSessionResponse registerQuizSetByFavorites(CreateQuizSessionRequest request) {
        Long accountId = request.getAccountId();
        Long folderId = request.getFolderId();

        // 1) 소유권 검증
        if (folderId != null) {
            boolean owned = userWordbookFolderQueryService.existsByIdAndAccountId(folderId, accountId);
            if (!owned) {
                throw new SecurityException("폴더가 없거나 권한이 없습니다.");
            }
        }

        // 2) 용어 조회
        List<Term> terms = (folderId == null)
                ? favoriteTermRepository.findTermsByAccount(accountId)
                : favoriteTermRepository.findTermsByAccountAndFolderStrict(accountId, folderId);

        if (terms.isEmpty()) {
            throw new IllegalArgumentException("즐겨찾기 용어가 없습니다.");
        }

        // 3) 최근 N일 내 본 Term 회피
        int N = (request.getAvoidRecentDays() == null || request.getAvoidRecentDays() <= 0) ? 30 : request.getAvoidRecentDays();
        List<Term> filteredTerms = terms;
        try {
            if (recentUsageService != null) {
                // 서비스가 있으면 서비스 사용
                Set<Long> recentTermIds = recentUsageService.findRecentTermIds(accountId, N);
                filteredTerms = terms.stream()
                        .filter(t -> !recentTermIds.contains(t.getId()))
                        .collect(Collectors.toList());
                if (filteredTerms.isEmpty()) {
                    log.info("최근 {}일 내 본 용어로 인해 후보가 비었습니다. 회피 미적용으로 대체합니다.", N);
                    filteredTerms = terms;
                }
            } else if (sessionAnswerRepository != null) {
                // 없으면 세션 답변 리포지토리로 폴백
                LocalDateTime since = LocalDateTime.now().minusDays(N);
                List<Long> recentIds = sessionAnswerRepository.findRecentTermIdsByAccountSince(accountId, since);
                Set<Long> recentSet = Set.copyOf(recentIds);
                filteredTerms = terms.stream()
                        .filter(t -> !recentSet.contains(t.getId()))
                        .collect(Collectors.toList());
                if (filteredTerms.isEmpty()) {
                    log.info("최근 {}일 내 본 용어로 인해 후보가 비었습니다(폴백). 회피 미적용으로 대체합니다.", N);
                    filteredTerms = terms;
                }
            } else {
                log.debug("[recent-exclude] RecentUsageService/SessionAnswerRepository 미주입 → 최근 제외 스킵");
            }
        } catch (Exception e) {
            log.warn("[recent-exclude] 최근 제외 처리 중 오류 → 회피 미적용으로 진행: {}", e.toString());
            filteredTerms = terms;
        }

        // 4) 시드/난이도 설정
        SeedMode seedMode = request.getSeedMode() == null ? SeedMode.AUTO : request.getSeedMode();
        String difficulty = (request.getDifficulty() == null) ? "MEDIUM" : request.getDifficulty();
        Long fixedSeed = request.getFixedSeed();

        // 5) 문항 생성 (필터링된 풀 사용)
        List<QuizQuestion> questions = autoQuizGenerator.generateQuestions(
                filteredTerms,
                request.getQuestionTypes(),
                request.getCount(),
                request.getMcqEach(),
                request.getOxEach(),
                request.getInitialsEach(),
                seedMode,
                request.getAccountId(),     // DAILY 재현성
                fixedSeed,                  // FIXED 재현성 (nullable)
                difficulty
        );

        if (questions.isEmpty()) throw new IllegalStateException("생성된 문제가 없습니다.");

        // 6) 세트 저장 + 문항 저장(Managed 상태로)
        QuizSet set = quizSetRepository.save(new QuizSet("[GEN] Favorites", true));
        questions.forEach(q -> q.setQuizSet(set));
        quizQuestionRepository.saveAll(questions);

        // 7) 보기 생성·저장 (옵션 재사용 회피는 RecentUsageService 있을 때만 적용)
        if (recentUsageService != null) {
            Set<String> recentOptionNorms = recentUsageService.findRecentChoiceNorms(accountId, N);
            autoQuizGenerator.createAndSaveChoicesForWithExclusion(
                    questions, seedMode, accountId, fixedSeed, recentOptionNorms
            );
        } else {
            autoQuizGenerator.createAndSaveChoicesFor(
                    questions, seedMode, accountId, fixedSeed
            );
        }

        return CreateQuizSessionResponse.of(
                set.getId(),
                questions.stream().map(QuizQuestion::getId).toList()
        );
    }

    /**
     * 카테고리에서 문항 수 만큼 용어를 선별.
     * - isRandom=true: DB 랜덤(함수 이름은 DB에 맞게 RAND()/RANDOM() 등) 정렬
     * - isRandom=false: 최근 등록순(id DESC) 예시
     */
    private List<Term> pickTermsByCategoryPolicy(Long categoryId, int count, boolean isRandom) {
        String base = "select t from Term t ";
        String where = (categoryId != null) ? "where t.category.id = :cid " : "";
        String order = isRandom
                ? "order by function('rand')"     // MySQL: rand, Postgres: random
                : "order by t.id desc";

        var q = em.createQuery(base + where + order, Term.class);
        if (categoryId != null) q.setParameter("cid", categoryId);
        q.setMaxResults(Math.max(1, Math.min(100, count)));
        return q.getResultList();
    }

    /**
     * 요청 DTO의 QuestionType(MIX/MCQ/OX/INITIAL) → 엔티티 QuestionType(CHOICE/OX/INITIALS) 매핑.
     * MIX는 termId 기반의 안정적 분배(CHOICE/OX/INITIALS)로 처리.
     */
    private QuestionType resolveQuestionType(CreateQuizSetByCategoryRequest.QuestionType reqType, Term term) {
        if (reqType == null) return QuestionType.CHOICE;

        switch (reqType) {
            case CHOICE:
                return QuestionType.CHOICE;
            case OX:
                return QuestionType.OX;
            case INITIALS:
                return QuestionType.INITIALS;
            case MIX:
            default:
                long seed = (term != null && term.getId() != null) ? term.getId() : System.nanoTime();
                int slot = (int) (Math.abs(seed) % 3);
                return switch (slot) {
                    case 0 -> QuestionType.CHOICE;
                    case 1 -> QuestionType.OX;
                    default -> QuestionType.INITIALS;
                };
        }
    }

    /** 문제 텍스트 생성 */
    private String makeQuestionText(Term term, QuestionType qType) {
        String title = (term != null && term.getTitle() != null) ? term.getTitle() : "제목 없음";
        String desc  = (term != null && term.getDescription() != null) ? term.getDescription() : "";

        switch (qType) {
            case INITIALS:
                // 초성 힌트 문제: "초성 힌트: ㄱㅅㅌ"처럼 문구 붙이기
                return "초성 힌트: " + toKoreanInitials(title);
            case OX:
                // OX는 아직 진위 생성 로직이 없다면 임시로 CHOICE처럼 정의-용어를 사용하거나 MIX에서 제외 권장
                return (desc.isBlank() ? title : desc);
            case CHOICE:
            default:
                // CHOICE 기본: 정의를 보여주고 보기에서 용어명을 고르게
                return (desc.isBlank() ? ("다음 설명에 해당하는 용어는? - " + title) : desc);
        }
    }

    /** 정답(있다면) 산출: 선택지 생성 시 확정하려면 null 유지 */
    private Integer makeCorrectAnswer(Term term, QuestionType qType) {
        return null;
    }

    /* ---------- 한글 초성 유틸 ---------- */

    private static final char HANGUL_BASE = 0xAC00;   // '가'
    private static final char HANGUL_END  = 0xD7A3;   // '힣'
    private static final char[] CHOSEONG = {
            'ㄱ','ㄲ','ㄴ','ㄷ','ㄸ','ㄹ','ㅁ','ㅂ','ㅃ','ㅅ',
            'ㅆ','ㅇ','ㅈ','ㅉ','ㅊ','ㅋ','ㅌ','ㅍ','ㅎ'
    };

    private String toKoreanInitials(String s) {
        if (s == null || s.isBlank()) return "";
        StringBuilder sb = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch >= HANGUL_BASE && ch <= HANGUL_END) {
                int syllableIndex = ch - HANGUL_BASE;
                int choIndex = syllableIndex / (21 * 28);
                sb.append(CHOSEONG[choIndex]);
            } else {
                sb.append(ch); // 한글 외 문자는 그대로
            }
        }
        return sb.toString();
    }

    private QuestionType mixByTerm(Term term) {
        long seed = (term != null && term.getId() != null) ? term.getId() : System.nanoTime();
        int slot = (int) (Math.abs(seed) % 3);
        return (slot == 0) ? QuestionType.CHOICE
                : (slot == 1) ? QuestionType.OX
                : QuestionType.INITIALS;
    }

    /** 선택지(보기) 생성: 정의→용어(정답) + 같은 풀에서 오답 3개 */
    private void createChoicesForQuestions(List<QuizQuestion> questions, List<Term> distractorPool) {
        if (questions == null || questions.isEmpty()) return;

        for (QuizQuestion q : questions) {
            Term term = q.getTerm();
            if (term == null) continue;

            if (q.getQuestionType() == QuestionType.INITIALS) {
                // 보기 없음
                continue;
            }

            if (q.getQuestionType() == QuestionType.OX) {
                QuizChoice o = new QuizChoice(q, "O", true,  "정답 해설");
                QuizChoice x = new QuizChoice(q, "X", false, "오답 해설");
                quizChoiceRepository.saveAll(List.of(o, x));
                // 팀 규칙에 맞게 인덱스 세팅 (예: O=1)
                q.setAnswerIndex(1);
                continue;
            }

            // === CHOICE ===
            String correctText = safeText(term.getTitle());

            List<Term> candidates = distractorPool.stream()
                    .filter(t -> !Objects.equals(t.getId(), term.getId()))
                    .collect(Collectors.toList());
            Collections.shuffle(candidates);

            LinkedHashSet<String> used = new LinkedHashSet<>();
            used.add(normalize(correctText));

            List<QuizChoice> toSave = new ArrayList<>();

            // 정답
            {
                QuizChoice c = new QuizChoice();
                c.setQuizQuestion(q);
                c.setChoiceText(correctText);
                c.setAnswer(true);
                c.setExplanation(safeText(term.getDescription()));
                toSave.add(c);
            }

            // 오답
            for (Term d : candidates) {
                if (toSave.size() >= 4) break; // 4지선다
                String txt = safeText(d.getTitle());
                String norm = normalize(txt);
                if (norm.isBlank() || used.contains(norm)) continue;
                used.add(norm);

                QuizChoice c = new QuizChoice();
                c.setQuizQuestion(q);
                c.setChoiceText(txt);
                c.setAnswer(false);
                toSave.add(c);
            }

            Collections.shuffle(toSave, new Random(q.getId()));
            quizChoiceRepository.saveAll(toSave);

            // 정답 위치(1-based) 기록
            int idx = 1;
            for (int i = 0; i < toSave.size(); i++) {
                if (toSave.get(i).isAnswer()) { idx = i + 1; break; }
            }
            q.setAnswerIndex(idx);
        }
    }

    private static String safeText(String s) { return (s == null ? "" : s.trim()); }
    private static String normalize(String s) {
        return (s == null) ? "" : s.trim().toLowerCase(Locale.ROOT);
    }

    /** 카테고리 기준 풀 만들기 (카테고리 없으면 질문에 딸린 term만 모아서 대체) */
    private List<Term> buildPoolForCategory(Long categoryId, List<Term> pickedTerms) {
        if (categoryId == null) return pickedTerms;
        return em.createQuery("select t from Term t where t.category.id = :cid", Term.class)
                .setParameter("cid", categoryId)
                .getResultList();
    }
}
