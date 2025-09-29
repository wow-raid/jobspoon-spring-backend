package com.wowraid.jobspoon.interview.controller;

import com.wowraid.jobspoon.account.service.AccountService;
import com.wowraid.jobspoon.interview.controller.request_form.InterviewCreateRequestForm;
import com.wowraid.jobspoon.interview.controller.response_form.InterviewCreateResponseForm;
import com.wowraid.jobspoon.interview.service.InterviewService;
import com.wowraid.jobspoon.interview.service.response.InterviewCreateResponse;
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
            @RequestBody InterviewCreateRequestForm  interviewCreateRequestForm) {

        Long accountId = redisCacheService.getValueByKey(userToken, Long.class);

        InterviewCreateResponse interviewCreateResponse = interviewService.createInterview(interviewCreateRequestForm, accountId);

        return  ResponseEntity.ok(InterviewCreateResponseForm.of(interviewCreateResponse));
    }




}
