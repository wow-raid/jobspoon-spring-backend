package com.wowraid.jobspoon.user_dashboard.controller;

import com.wowraid.jobspoon.user_dashboard.controller.request.CreateInterviewRequest;
import com.wowraid.jobspoon.user_dashboard.controller.response.InterviewResponse;
import com.wowraid.jobspoon.user_dashboard.entity.Interview;
import com.wowraid.jobspoon.user_dashboard.service.InterviewService;
import com.wowraid.jobspoon.user_dashboard.service.TokenAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/interviews")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;
    private final TokenAccountService tokenAccountService;

    @PostMapping
    public ResponseEntity<InterviewResponse> createInterview(
            @RequestHeader("Authorization") String userToken,
            @RequestBody CreateInterviewRequest request
    ) {
        Long accountId = tokenAccountService.resolveAccountId(userToken);
        Interview interview = interviewService.createInterview(
                accountId,
                request.getTopic(),
                request.getExperienceLevel(),
                request.getProjectExperience(),
                request.getAcademicBackground(),
                request.getTechStack(),
                request.getCompanyName()
        );

        return ResponseEntity.ok(InterviewResponse.fromEntity(interview));
    }

    @GetMapping("/my")
    public ResponseEntity<List<InterviewResponse>> listMyInterviews(
            @RequestHeader("Authorization") String userToken,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long accountId = tokenAccountService.resolveAccountId(userToken);

        List<InterviewResponse> responseList = interviewService.listInterview(accountId, page, size)
                .stream()
                .map(InterviewResponse::fromEntity) // 정적 메소드 활용
                .toList();

        return ResponseEntity.ok(responseList);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInterview(
            @RequestHeader("Authorization") String userToken,
            @PathVariable Long id
    ) {
        Long accountId = tokenAccountService.resolveAccountId(userToken);
        interviewService.deleteInterview(accountId, id);
        return ResponseEntity.noContent().build();
    }
}
