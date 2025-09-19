package com.wowraid.jobspoon.user_dashboard.controller;

import com.wowraid.jobspoon.user_dashboard.controller.response.InterviewResultResponse;
import com.wowraid.jobspoon.user_dashboard.entity.InterviewResult;
import com.wowraid.jobspoon.user_dashboard.service.InterviewResultService;
import com.wowraid.jobspoon.user_dashboard.service.TokenAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/interview-results")
@RequiredArgsConstructor
public class InterviewResultController {

    private final InterviewResultService interviewResultService;
    private final TokenAccountService tokenAccountService;

    /**
     * 인터뷰 종료 → 결과 저장
     */
    @PostMapping("/{interviewId}")
    public ResponseEntity<InterviewResultResponse> saveInterviewResult(
            @RequestHeader("Authorization") String userToken,
            @PathVariable Long interviewId
    ) {
        Long accountId = tokenAccountService.resolveAccountId(userToken);
        InterviewResult result = interviewResultService.saveInterviewResult(accountId, interviewId);
        return ResponseEntity.ok(InterviewResultResponse.fromEntity(result));
    }

    /**
     * 내 마지막 인터뷰 결과 조회
     */
    @GetMapping("/last")
    public ResponseEntity<InterviewResultResponse> getLastInterviewResult(
            @RequestHeader("Authorization") String userToken
    ) {
        Long accountId = tokenAccountService.resolveAccountId(userToken);
        InterviewResult result = interviewResultService.getLastInterviewResult(accountId);
        return ResponseEntity.ok(InterviewResultResponse.fromEntity(result));
    }

    /**
     * QA 점수 저장
     */
    @PutMapping("/{resultId}/qa")
    public ResponseEntity<Void> saveQAScoreList(
            @PathVariable Long resultId,
            @RequestBody String qaScoresJson
    ) {
        interviewResultService.saveQAScoreList(resultId, qaScoresJson);
        return ResponseEntity.noContent().build();
    }

    /**
     * 헥사곤 점수 저장
     */
    @PutMapping("/{resultId}/hexagon")
    public ResponseEntity<Void> recordHexagonEvaluation(
            @PathVariable Long resultId,
            @RequestBody String evaluationScoresJson
    ) {
        interviewResultService.recordHexagonEvaluation(resultId, evaluationScoresJson);
        return ResponseEntity.noContent().build();
    }
}
