package com.wowraid.jobspoon.quiz.service;

import com.wowraid.jobspoon.quiz.entity.QuizQuestion;
import com.wowraid.jobspoon.quiz.entity.QuizSet;
import com.wowraid.jobspoon.quiz.entity.enums.QuestionType;
import com.wowraid.jobspoon.quiz.entity.enums.SeedMode;
import com.wowraid.jobspoon.quiz.repository.QuizQuestionRepository;
import com.wowraid.jobspoon.quiz.repository.QuizSetRepository;
import com.wowraid.jobspoon.quiz.repository.SessionAnswerRepository;
import com.wowraid.jobspoon.quiz.service.generator.AutoQuizGenerator;
import com.wowraid.jobspoon.quiz.service.request.CreateQuizSessionRequest;
import com.wowraid.jobspoon.quiz.service.request.CreateQuizSetByCategoryRequest;
import com.wowraid.jobspoon.quiz.service.response.BuiltQuizSetResponse;
import com.wowraid.jobspoon.quiz.service.response.CreateQuizSessionResponse;
import com.wowraid.jobspoon.quiz.service.response.CreateQuizSetByCategoryResponse;
import com.wowraid.jobspoon.term.entity.Category;
import com.wowraid.jobspoon.term.entity.Term;
import com.wowraid.jobspoon.term.repository.CategoryRepository;
import com.wowraid.jobspoon.user_term.repository.FavoriteTermRepository;
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

    /** 선택 주입: 있으면 사용(최근 옵션 텍스트 재사용 회피 등), 없으면 SessionAnswerRepository로 폴백 */
    @Autowired(required = false)
    private RecentUsageService recentUsageService;

    @PersistenceContext
    private EntityManager em;

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

        // 2) 타이틀 확정 (람다 캡처 이슈 회피: if로 계산)
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

            QuizQuestion q = new QuizQuestion(
                    term,
                    category,
                    qType,
                    makeQuestionText(term, qType),
                    makeCorrectAnswer(term, qType),
                    quizSet,
                    ++order
            );
            q.setRandom(request.isRandom());
            questions.add(q);
        }
        quizQuestionRepository.saveAll(questions);

        // 6) questionIds 재조회 (정렬: id ASC)
        List<Long> questionIds = em.createQuery(
                        "select q.id from QuizQuestion q where q.quizSet.id = :sid order by q.id",
                        Long.class
                )
                .setParameter("sid", quizSet.getId())
                .getResultList();

        // 7) 결과 반환
        return BuiltQuizSetResponse.builder()
                .quizSetId(quizSet.getId())
                .questionIds(questionIds)
                .title(quizSet.getTitle())
                .isRandom(quizSet.isRandom())
                .totalQuestions(questionIds.size())
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
            case MCQ:
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
        switch (qType) {
            case INITIALS:
                return toKoreanInitials(title); // 초성 힌트
            case OX:
            case CHOICE:
            default:
                return title; // 필요 시 템플릿 문구로 변경
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
}
