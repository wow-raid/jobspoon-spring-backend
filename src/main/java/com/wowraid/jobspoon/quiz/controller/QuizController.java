package com.wowraid.jobspoon.quiz.controller;

import com.wowraid.jobspoon.quiz.controller.request_form.*;
import com.wowraid.jobspoon.quiz.controller.response_form.*;
import com.wowraid.jobspoon.quiz.entity.QuizChoice;
import com.wowraid.jobspoon.quiz.entity.enums.SeedMode;
import com.wowraid.jobspoon.quiz.service.*;
import com.wowraid.jobspoon.quiz.service.request.CreateQuizChoiceRequest;
import com.wowraid.jobspoon.quiz.service.request.CreateQuizQuestionRequest;
import com.wowraid.jobspoon.quiz.service.request.CreateQuizSetByCategoryRequest;
import com.wowraid.jobspoon.quiz.service.request.CreateQuizSetByFolderRequest;
import com.wowraid.jobspoon.quiz.service.response.*;
import com.wowraid.jobspoon.redis_cache.RedisCacheService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class QuizController {

    private final QuizQuestionService quizQuestionService;
    private final QuizSetService quizSetService;
    private final QuizChoiceService quizChoiceService;
    private final RedisCacheService redisCacheService;
    private final UserQuizAnswerService userQuizAnswerService;
    private final UserQuizSessionQueryService userQuizSessionQueryService;
    private final UserQuizEraseService userQuizEraseService;
    private final QuizSetQueryService quizSetQueryService;

    /** 공통: 쿠키/헤더에서 토큰 추출 후 Redis에서 accountId 조회(없으면 null) */
    // -> 정책 변경: 쿠키 전용으로 단순화
    private Long resolveAccountId(String userToken) {
        if (userToken == null || userToken.isBlank()) {
            return null;
        }
        return redisCacheService.getValueByKey(userToken, Long.class); // TTL 만료/무효면 null
    }

    // 용어 기반 퀴즈 문제 등록하기
    @PostMapping("/terms/{termId}/quiz-questions")
    public ResponseEntity<CreateQuizQuestionResponseForm> createQuizQuestion (
            @PathVariable("termId") Long termId,
            @Valid @RequestBody CreateQuizQuestionRequestForm requestForm,
            @CookieValue(name = "userToken", required = false) String userToken
    ) {
        Long accountId = resolveAccountId(userToken);
        if (accountId == null) {
            log.warn("인증 실패: 계정 식별 불가");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        log.info("용어에 대한 퀴즈 문제 등록 요청 - termId: {}, accountId: {}",  termId, accountId);

        CreateQuizQuestionRequest request = requestForm.toCreateQuizQuestionRequest(termId);
        try {
            CreateQuizQuestionResponse response = quizQuestionService.registerQuizQuestion(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(CreateQuizQuestionResponseForm.from(response));
        } catch (Exception e) {
            log.error("퀴즈 세트 생성 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 카테고리 기반 퀴즈 세트 자동 생성
    @PostMapping("/quiz-sets")
    public ResponseEntity<CreateQuizSetByCategoryResponseForm> createByCategory (
            @Valid @RequestBody CreateQuizSetByCategoryRequestForm requestForm,
            @CookieValue(name = "userToken", required = false) String userToken
    ) {
        Long accountId = resolveAccountId(userToken);
        if (accountId == null) {
            log.warn("인증 실패: 계정 식별 불가");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        log.info("카테고리 기반 퀴즈 세트 구성 요청 - accountId: {}", accountId);

        CreateQuizSetByCategoryRequest request = requestForm.toCategoryBasedRequest();
        try {
            CreateQuizSetByCategoryResponse response = quizSetService.registerQuizSetByCategory(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(CreateQuizSetByCategoryResponseForm.from(response));
        } catch (Exception e) {
            log.error("퀴즈 세트 생성 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 퀴즈 보기 생성
    @PostMapping("/quiz-questions/{quizQuestionId}/choices")
    public ResponseEntity<CreateQuizChoiceListResponseForm> createQuizChoices(
            @PathVariable("quizQuestionId") Long quizQuestionId,
            @Valid @RequestBody List<@Valid CreateQuizChoiceRequest> requestList,
            @CookieValue(name = "userToken", required = false) String userToken
    ) {
        Long accountId = resolveAccountId(userToken);
        if (accountId == null) {
            log.warn("인증 실패: 계정 식별 불가");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (requestList == null || requestList.isEmpty()) {
            log.warn("요청 유효성 오류: choices 리스트가 비어있음");
            return ResponseEntity.badRequest().build();
        }

        log.info("퀴즈 보기 생성 요청 - quizQuestionId: {}, accountId: {}", quizQuestionId, accountId);

        try {
            List<QuizChoice> savedChoices = quizChoiceService.registerQuizChoices(quizQuestionId, requestList);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(CreateQuizChoiceListResponseForm.from(savedChoices));
        } catch (Exception e) {
            log.error("퀴즈 보기 생성 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 즐겨찾기 용어로 퀴즈 세션 만들기
    @PostMapping("/me/quiz/sessions/from-favorites")
    public ResponseEntity<CreateQuizSessionResponseForm> createFromFavorites(
            @Valid @RequestBody CreateQuizSessionRequestForm requestForm,
            @CookieValue(name = "userToken", required = false) String userToken
    ) {
        Long accountId = resolveAccountId(userToken);
        if (accountId == null) {
            log.warn("인증 실패: 계정 식별 불가");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            CreateQuizSessionResponse response = quizSetService.registerQuizSetByFavorites(requestForm.toServiceRequest(accountId));
            StartUserQuizSessionResponse started =
                    userQuizAnswerService.startFromQuizSet(
                            accountId,
                            response.getQuizSetId(),
                            response.getQuestionIds(),
                            requestForm.getSeedMode(),
                            requestForm.getFixedSeed()
                    );
            return ResponseEntity.status(HttpStatus.CREATED).body(CreateQuizSessionResponseForm.from(started));
        } catch (IllegalArgumentException e) {
            log.warn("요청 오류", e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("즐겨찾기 기반 세션 생성 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 카테고리 기반 퀴즈 세션 만들기
    @PostMapping("/me/quiz/sessions/from-category")
    public ResponseEntity<CreateQuizSessionResponseForm> createFromCategory(
            @Valid @RequestBody StartQuizSessionByCategoryRequestForm requestForm,
            @CookieValue(name = "userToken", required = false) String userToken
    ) {
        Long accountId = resolveAccountId(userToken);
        if (accountId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        try {
            // 1) 세트 구성 (questionIds 포함)
            var built = quizSetService.registerQuizSetByCategory(requestForm.toCategoryBasedRequest());

            // 2) 문자열 seedMode -> enum 변환 (대소문자 무시, 예외 시 AUTO)
            SeedMode seedMode;
            try {
                seedMode = SeedMode.valueOf(requestForm.getSeedMode().toUpperCase());
            } catch (Exception e) {
                seedMode = SeedMode.AUTO;
            }

            // 3) 세션 시작
            StartUserQuizSessionResponse started = userQuizAnswerService.startFromQuizSet(
                    accountId,
                    built.getQuizSetId(),
                    built.getQuestionIds(),
                    seedMode,
                    requestForm.getFixedSeed()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(CreateQuizSessionResponseForm.from(started));
        } catch (IllegalArgumentException e) {
            log.warn("요청 오류", e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("카테고리 기반 세션 생성 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 단어장(스푼노트) 폴더 기반 퀴즈 세션 즉시 만들기
    @PostMapping("/me/quiz/sessions/from-folder")
    public ResponseEntity<CreateQuizSessionResponseForm> createFromFolder(
            @Valid @RequestBody CreateQuizSessionFromFolderRequestForm requestForm,
            @CookieValue(name = "userToken", required = false) String userToken
    ) {
        Long accountId = resolveAccountId(userToken);
        if (accountId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        SeedMode mode = resolveSeedMode(requestForm.getSeedMode(), requestForm.getFixedSeed());

        try {
            // 1) 세트 구성
            CreateQuizSetByFolderRequest request = requestForm.toFolderBasedRequest(accountId);
            BuiltQuizSetResponse built = quizSetService.registerQuizSetByFolderReturningQuestions(request);

            // 2) 세션 시작
            StartUserQuizSessionResponse started = userQuizAnswerService.startFromQuizSet(
                    accountId,
                    built.getQuizSetId(),
                    built.getQuestionIds(),
                    mode,
                    requestForm.getFixedSeed()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(CreateQuizSessionResponseForm.from(started));

        } catch (SecurityException e) {
            log.warn("[from-folder] 권한/소유권 오류: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IllegalArgumentException e) {
            log.warn("[from-folder] 요청 오류: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("[from-folder] 서버 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 프론트에서 선택한 것(폴더/카테고리)을 그대로 반영해 퀴즈 세션 시작하기 (통합 엔드포인트)
    @PostMapping("/me/quiz/sessions/start")
    public ResponseEntity<CreateQuizSessionResponseForm> startQuizUnified(
            @Valid @RequestBody StartQuizSessionUnifiedRequestForm requestForm,
            @CookieValue(name = "userToken", required = false) String userToken
    ) {
        Long accountId = resolveAccountId(userToken);
        if (accountId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        // seedMode 통일 처리
        SeedMode mode = resolveSeedMode(requestForm.getSeedMode(), requestForm.getFixedSeed());

        try {
            String source = requestForm.getSource() == null ? "" : requestForm.getSource().trim().toLowerCase();

            switch (source) {
                case "folder": {
                    if (requestForm.getFolderId() == null) {
                        log.warn("[unified] source=folder인데 folderId 누락");
                        return ResponseEntity.badRequest().build();
                    }

                    // 1) 세트 구성 (기존 폼으로 위임 변환)
                    var folderForm = requestForm.toFolderForm();
                    var built = quizSetService.registerQuizSetByFolderReturningQuestions(folderForm.toFolderBasedRequest(accountId));

                    // 2) 세션 시작
                    StartUserQuizSessionResponse started = userQuizAnswerService.startFromQuizSet(
                            accountId,
                            built.getQuizSetId(),
                            built.getQuestionIds(),
                            mode,
                            requestForm.getFixedSeed()
                    );
                    return ResponseEntity.status(HttpStatus.CREATED).body(CreateQuizSessionResponseForm.from(started));
                }

                case "category": {
                    if (requestForm.getCategoryId() == null) {
                        log.warn("[unified] source = category인데 categoryId가 누락");
                        return ResponseEntity.badRequest().build();
                    }

                    // 1) 세트 구성
                    var categoryForm = requestForm.toCategoryForm();
                    var built = quizSetService.registerQuizSetByCategory(categoryForm.toCategoryBasedRequest());

                    // 2) 세션 시작
                    StartUserQuizSessionResponse started = userQuizAnswerService.startFromQuizSet(
                            accountId,
                            built.getQuizSetId(),
                            built.getQuestionIds(),
                            mode,
                            requestForm.getFixedSeed()
                    );
                    return ResponseEntity.status(HttpStatus.CREATED).body(CreateQuizSessionResponseForm.from(started));
                }

                default:
                    log.warn("[unified] source 값이 유효하지 않음: {}", source);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        } catch (SecurityException e) {
            log.warn("[unified] 권한/소유권 오류: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IllegalArgumentException e) {
            log.warn("[unified] 요청 유효성 오류: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.warn("[unified] 서버 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 자신이 푼 답안을 제출하여 점수 확인하기
    @PostMapping("/me/quiz/sessions/{sessionId}/submit")
    public ResponseEntity<SubmitQuizSessionResponseForm> submitQuizSession(
            @PathVariable Long sessionId,
            @Valid @RequestBody SubmitQuizSessionRequestForm requestForm,
            @CookieValue(name = "userToken", required = false) String userToken
    ) {
        Long accountId = resolveAccountId(userToken);
        if (accountId == null) {
            log.warn("인증 실패: 계정 식별 불가");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            var response = userQuizAnswerService.submitSession(sessionId, accountId, requestForm);
            return ResponseEntity.ok(response);
        } catch (SecurityException e) {
            log.warn("세션 접근 거부", e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.warn("세션 제출 유효성 오류", e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("세션 제출 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 오답노트만 다시 풀기
    @PostMapping("/me/quiz/sessions/{sessionId}/retry-wrong")
    public ResponseEntity<CreateQuizSessionResponseForm> retryWrongOnly(
            @PathVariable Long sessionId,
            @CookieValue(name = "userToken", required = false) String userToken
    ) {
        Long accountId = resolveAccountId(userToken);
        if (accountId == null) {
            log.warn("인증 실패: 계정 식별 불가");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            StartUserQuizSessionResponse started = userQuizAnswerService.startRetryWrongOnly(sessionId, accountId);
            return ResponseEntity.status(HttpStatus.CREATED).body(CreateQuizSessionResponseForm.from(started));
        } catch(SecurityException e) {
            log.warn("세션 접근 거부", e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.warn("오답세션 생성 유효성 오류", e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("오답세션 생성 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 세션 요약 단건 조회하기
    @GetMapping("/me/quiz/sessions/{sessionId}")
    public ResponseEntity<SessionSummaryResponseForm> getSessionSummary(
            @PathVariable Long sessionId,
            @CookieValue(name = "userToken", required = false) String userToken
    ) {
        Long accountId = resolveAccountId(userToken);
        if (accountId == null) {
            log.warn("인증 실패: 계정 식별 불가");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var summary = userQuizSessionQueryService.getSummary(sessionId, accountId);
        return ResponseEntity.ok(summary);
    }

    // 세션 문제 페이지 조회하기(페이지네이션으로 분리해 조회)
    @GetMapping("/me/quiz/sessions/{sessionId}/items")
    public ResponseEntity<SessionItemsPageResponseForm> getSessionItems(
            @PathVariable Long sessionId,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(name = "includeAnswers", defaultValue = "false") boolean includeAnswers,
            @CookieValue(name = "userToken", required = false) String userToken
    ) {
        Long accountId = resolveAccountId(userToken);
        if (accountId == null) {
            log.warn("인증 실패: 계정 식별 불가");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        limit = Math.max(1, Math.min(100, limit));

        var page = userQuizSessionQueryService.getSessionItems(sessionId, accountId, offset, limit);
        return ResponseEntity.ok(page);
    }

    // 최근 세션 목록 조회하기
    @GetMapping("/me/quiz/sessions")
    public ResponseEntity<SessionListResponseForm> listMySessions(
            @RequestParam(name = "limit", defaultValue = "20") int limit,
            @RequestParam(name = "status", required = false) String status, // SUBMITTED/IN_PROGRESS/null
            @CookieValue(name = "userToken", required = false) String userToken
    ) {
        Long accountId = resolveAccountId(userToken);
        if (accountId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (limit <= 0 || limit > 100) limit = 20;
        var list = userQuizSessionQueryService.listMySessions(accountId, limit, status);
        return ResponseEntity.ok(list);
    }

    // 세션 리뷰(정답/해설/내 선택)
    @GetMapping("/me/quiz/sessions/{sessionId}/review")
    public ResponseEntity<SessionReviewResponseForm> getSessionReview(
            @PathVariable Long sessionId,
            @CookieValue(name = "userToken", required = false) String userToken
    ) {
        Long accountId = resolveAccountId(userToken);
        if (accountId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var review = userQuizSessionQueryService.getReview(sessionId, accountId);
        return ResponseEntity.ok(review);
    }

    @GetMapping("/quiz/sets/{setId}/questions")
    public ResponseEntity<List<ChoiceQuestionResponseForm>> getChoiceQuestions(
            @PathVariable Long setId,
            @RequestParam(required = false, defaultValue = "CHOICE") String part
    ) {
        if (!"CHOICE".equalsIgnoreCase(part)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        var reads = quizSetQueryService.findChoiceQuestionsBySetId(setId);
        var body = reads.stream().map(ChoiceQuestionResponseForm::from).toList();
        return ResponseEntity.ok(body);
    }


    /** 정책: 소유권 위반/존재하지 않음은 404로 숨김 */
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Void> handleSecurityException(SecurityException ex) {
        log.warn("보안/소유권 오류 → 404 변환: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    /** 만료 등 상태 충돌은 409 */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Void> handleIllegalState(IllegalStateException ex) {
        log.warn("상태 충돌(409): {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    private SeedMode resolveSeedMode(String raw, Long fixedSeed) {
        if (raw != null && !raw.isBlank()) {
            try { return SeedMode.valueOf(raw.trim().toUpperCase()); }
            catch (Exception ignore) { /* fall-through */ }
        }
        if (fixedSeed != null) return SeedMode.FIXED;
        return SeedMode.AUTO;
    }

    /**
     * 내부(Admin) 호출용: quiz 도메인 데이터(해당 계정 것만) 삭제
     * - user_quiz_session, session_answer(해당 세션들), user_wrong_note(해당 계정) 삭제
     * - '고아 quiz_set'이 되면 세트/문항/보기까지 함께 정리
     *
     * 주의: quiz_set 자체는 계정 컬럼이 없어 타 계정이 공유 중일 수 있음.
     *      따라서 '현재 어떤 세션도 참조하지 않는' 세트만 정리
     */
    @DeleteMapping("/internal/admin/accounts/{accountId}/quiz:erase")
    public ResponseEntity<?> eraseQuizByAccount(@PathVariable Long accountId) {
        var result = userQuizEraseService.eraseByAccountId(accountId);

        Map<String, Object> body = Map.of(
                "accountId", accountId,
                "deleted", Map.of(
                        "wrong_note",          result.getWrongNotes(),
                        "session_answer",      result.getSessionAnswers(),
                        "user_quiz_session",   result.getSessions(),
                        "orphan_quiz_choice",  result.getOrphanChoices(),
                        "orphan_quiz_question",result.getOrphanQuestions(),
                        "orphan_quiz_set",     result.getOrphanSets()
                )
        );

        log.info("[quiz:erase] {}", body);
        return ResponseEntity.ok(body);
    }
}
