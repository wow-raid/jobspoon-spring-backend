package com.wowraid.jobspoon.quiz.controller;

import com.wowraid.jobspoon.quiz.controller.response_form.CreateQuizSessionResponseForm;
import com.wowraid.jobspoon.quiz.entity.enums.JobRole;
import com.wowraid.jobspoon.quiz.entity.enums.QuizPartType;
import com.wowraid.jobspoon.quiz.entity.enums.SessionMode;
import com.wowraid.jobspoon.quiz.service.DailyQuizService;
import com.wowraid.jobspoon.quiz.service.UserQuizAnswerService;
import com.wowraid.jobspoon.quiz.service.response.StartUserQuizSessionResponse;
import com.wowraid.jobspoon.redis_cache.RedisCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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

        try {
            var built = dailyQuizService.resolve(d, p, r);

            var body = new java.util.LinkedHashMap<String, Object>();
            body.put("exists", true);
            body.put("date", d.toString());
            body.put("part", p.name());
            body.put("role", r.name());
            body.put("quizSetId", built.getQuizSetId());
            body.put("title", built.getTitle());
            body.put("totalQuestions", built.getTotalQuestions());
            body.put("random", built.isRandom());
            body.put("questionIds", built.getQuestionIds());

            return ResponseEntity.ok(body);

        } catch (IllegalArgumentException e) {
            var body = new java.util.LinkedHashMap<String, Object>();
            body.put("exists", false);
            body.put("date", d.toString());
            body.put("part", p.name());
            body.put("role", r.name());
            body.put("message", "해당 날짜/파트 공개 세트가 없습니다.");
            return ResponseEntity.ok(body);
        }
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

        try {
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
            return ResponseEntity.status(HttpStatus.CREATED).body(CreateQuizSessionResponseForm.from(started));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}