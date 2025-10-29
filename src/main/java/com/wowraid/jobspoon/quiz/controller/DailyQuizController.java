package com.wowraid.jobspoon.quiz.controller;

import com.wowraid.jobspoon.quiz.controller.response_form.CreateQuizSessionResponseForm;
import com.wowraid.jobspoon.quiz.entity.enums.JobRole;
import com.wowraid.jobspoon.quiz.entity.enums.QuizPartType;
import com.wowraid.jobspoon.quiz.service.DailyQuizService;
import com.wowraid.jobspoon.quiz.service.UserQuizAnswerService;
import com.wowraid.jobspoon.quiz.service.response.StartUserQuizSessionResponse;
import com.wowraid.jobspoon.redis_cache.RedisCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/daily")
public class DailyQuizController {
    private final DailyQuizService dailyQuizService;
    private final RedisCacheService redisCacheService;
    private final UserQuizAnswerService userQuizAnswerService;

    private Long resolveAccountId(String userToken) {
        if (userToken == null || userToken.isBlank()) {
            return null;
        }
        return redisCacheService.getValueByKey(userToken, Long.class);
    }

    @GetMapping("/plan")
    public ResponseEntity<?> getDailyPlan(
            @RequestParam(required = false) String date,
            @RequestParam String part,
            @RequestParam(defaultValue = "GENERAL") String role
    ) {
        var zone = java.time.ZoneId.of("Asia/Seoul");
        var d = (date == null || date.isBlank())
                ? java.time.LocalDate.now(zone)
                : java.time.LocalDate.parse(date);

        var p = QuizPartType.fromParam(part);
        var r = JobRole.from(role);

        var built = dailyQuizService.resolve(d, p, r);
        return ResponseEntity.ok(built);
    }

    @PostMapping("/sessions/start")
    public ResponseEntity<CreateQuizSessionResponseForm> startDailySession(
            @RequestParam(required = false) String date,
            @RequestParam String part,
            @RequestParam(defaultValue = "GENERAL") String role,
            @RequestParam(required = false) String seedMode,
            @RequestParam(required = false) Long fixedSeed,
            @CookieValue(name = "userToken", required = false) String userToken
    ) {
        Long accountId = resolveAccountId(userToken);
        if (accountId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        var zone = java.time.ZoneId.of("Asia/Seoul");
        var d = (date == null || date.isBlank())
                ? java.time.LocalDate.now(zone)
                : java.time.LocalDate.parse(date);
        var p = QuizPartType.fromParam(part);
        var r = JobRole.from(role);

        var built = dailyQuizService.resolve(d, p, r);

        // SeedMode 결정
        var mode = (seedMode == null || seedMode.isBlank())
                ? (fixedSeed != null ? com.wowraid.jobspoon.quiz.entity.enums.SeedMode.FIXED
                : com.wowraid.jobspoon.quiz.entity.enums.SeedMode.AUTO)
                : com.wowraid.jobspoon.quiz.entity.enums.SeedMode.valueOf(seedMode.toUpperCase());

        // 실제 세션 생성
        StartUserQuizSessionResponse started = userQuizAnswerService.startFromQuizSet(
                accountId,
                built.getQuizSetId(),
                built.getQuestionIds(),
                mode,
                fixedSeed
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CreateQuizSessionResponseForm.from(started));
    }
}