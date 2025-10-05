package com.wowraid.jobspoon.quiz.service;

import com.wowraid.jobspoon.quiz.entity.QuizQuestion;
import com.wowraid.jobspoon.quiz.entity.QuizSet;
import com.wowraid.jobspoon.quiz.entity.enums.SeedMode;
import com.wowraid.jobspoon.quiz.repository.QuizQuestionRepository;
import com.wowraid.jobspoon.quiz.repository.QuizSetRepository;
import com.wowraid.jobspoon.quiz.repository.SessionAnswerRepository;
import com.wowraid.jobspoon.quiz.service.generator.AutoQuizGenerator;
import com.wowraid.jobspoon.quiz.service.request.CreateQuizSessionRequest;
import com.wowraid.jobspoon.quiz.service.request.CreateQuizSetByCategoryRequest;
import com.wowraid.jobspoon.quiz.service.response.CreateQuizSessionResponse;
import com.wowraid.jobspoon.quiz.service.response.CreateQuizSetByCategoryResponse;
import com.wowraid.jobspoon.term.entity.Category;
import com.wowraid.jobspoon.term.entity.Term;
import com.wowraid.jobspoon.term.repository.CategoryRepository;
import com.wowraid.jobspoon.user_term.repository.FavoriteTermRepository;
import com.wowraid.jobspoon.user_term.service.UserWordbookFolderQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired; // optional bean
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
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
    private final UserWordbookFolderQueryService userWordbookFolderQueryService; // 캐시 무효화용(필요시)
    private final SessionAnswerRepository sessionAnswerRepository;               // 테스트가 기대하는 의존성

    /** 선택 주입: 있으면 사용(최근 옵션 텍스트 재사용 회피 등), 없으면 SessionAnswerRepository로 폴백 */
    @Autowired(required = false)
    private RecentUsageService recentUsageService;

    @Override
    public CreateQuizSetByCategoryResponse registerQuizSetByCategory(CreateQuizSetByCategoryRequest request) {
        // categoryId로 Category 조회
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리를 찾을 수 없습니다."));

        // 퀴즈 문제 리스트 조회
        List<QuizQuestion> questions = quizSetRepository.findByCategory(category);

        // QuizSet 객체 생성
        QuizSet quizSet = new QuizSet(request.getTitle(), category, request.isRandom());

        for (QuizQuestion question : questions) {
            question.setQuizSet(quizSet);
        }

        // Repository에 저장(cascade 사용 안 하면 questions도 직접 저장 필요)
        QuizSet savedQuizSet = quizSetRepository.save(quizSet);
        quizQuestionRepository.saveAll(questions); // cascade 안 쓴 경우 필요

        return CreateQuizSetByCategoryResponse.from(savedQuizSet);
    }

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
}
