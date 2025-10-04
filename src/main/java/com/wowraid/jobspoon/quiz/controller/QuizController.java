package com.wowraid.jobspoon.quiz.controller;

import com.wowraid.jobspoon.quiz.controller.request_form.CreateQuizQuestionRequestForm;
import com.wowraid.jobspoon.quiz.controller.request_form.CreateQuizSessionRequestForm;
import com.wowraid.jobspoon.quiz.controller.request_form.CreateQuizSetByCategoryRequestForm;
import com.wowraid.jobspoon.quiz.controller.request_form.SubmitQuizSessionRequestForm;
import com.wowraid.jobspoon.quiz.controller.response_form.*;
import com.wowraid.jobspoon.quiz.entity.QuizChoice;
import com.wowraid.jobspoon.quiz.service.*;
import com.wowraid.jobspoon.quiz.service.request.CreateQuizChoiceRequest;
import com.wowraid.jobspoon.quiz.service.request.CreateQuizQuestionRequest;
import com.wowraid.jobspoon.quiz.service.request.CreateQuizSetByCategoryRequest;
import com.wowraid.jobspoon.quiz.service.response.CreateQuizQuestionResponse;
import com.wowraid.jobspoon.quiz.service.response.CreateQuizSessionResponse;
import com.wowraid.jobspoon.quiz.service.response.CreateQuizSetByCategoryResponse;
import com.wowraid.jobspoon.quiz.service.response.StartUserQuizSessionResponse;
import com.wowraid.jobspoon.redis_cache.RedisCacheService;
import com.wowraid.jobspoon.user_term.service.UserWordbookFolderQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            @RequestParam(defaultValue = "10") int limit,
            @CookieValue(name = "userToken", required = false) String userToken
    ) {
        Long accountId = resolveAccountId(userToken);
        if (accountId == null) {
            log.warn("인증 실패: 계정 식별 불가");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (limit <= 0 || limit > 100) limit = 10;

        var page = userQuizSessionQueryService.getSessionItems(sessionId, accountId, offset, limit);
        return ResponseEntity.ok(page);
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
}
