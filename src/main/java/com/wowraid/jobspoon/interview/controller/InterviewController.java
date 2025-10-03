package com.wowraid.jobspoon.interview.controller;

import com.wowraid.jobspoon.account.service.AccountService;
import com.wowraid.jobspoon.interview.controller.request_form.InterviewCreateRequestForm;
import com.wowraid.jobspoon.interview.controller.request_form.InterviewProgressRequestForm;
import com.wowraid.jobspoon.interview.controller.response_form.InterviewCreateResponseForm;
import com.wowraid.jobspoon.interview.controller.response_form.InterviewProgressResponseForm;
import com.wowraid.jobspoon.interview.entity.InterviewType;
import com.wowraid.jobspoon.interview.service.InterviewService;
import com.wowraid.jobspoon.interview.service.response.InterviewCreateResponse;
import com.wowraid.jobspoon.interview.service.response.InterviewProgressResponse;
import com.wowraid.jobspoon.redis_cache.RedisCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/interview")
@RequiredArgsConstructor
public class InterviewController {

    private final RedisCacheService redisCacheService;
    private final InterviewService interviewService;

    @PostMapping("/create")
    public ResponseEntity<InterviewCreateResponseForm> interviewCreate(
            @CookieValue(name = "userToken", required = false) String userToken,
            @RequestBody InterviewCreateRequestForm interviewCreateRequestForm) {

        Long accountId = redisCacheService.getValueByKey(userToken, Long.class);

        InterviewCreateResponse interviewCreateResponse = interviewService.createInterview(interviewCreateRequestForm, accountId, userToken);


        return ResponseEntity.ok(InterviewCreateResponseForm.of(interviewCreateResponse));

    }


    @PostMapping("/progress")
    public ResponseEntity<InterviewProgressResponseForm> progressInterview(
            @CookieValue(name = "userToken", required = false) String userToken,
            @RequestBody InterviewProgressRequestForm interviewProgressRequestForm
    ) {
        InterviewProgressResponse interviewProgressResponse = interviewService.execute(
                interviewProgressRequestForm.getInterviewType(), interviewProgressRequestForm, userToken);

        return ResponseEntity.ok(interviewProgressResponse.toInterviewProgressResponseForm());
    }






}
