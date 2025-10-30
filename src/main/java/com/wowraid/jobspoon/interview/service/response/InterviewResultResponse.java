package com.wowraid.jobspoon.interview.service.response;

import com.wowraid.jobspoon.interview.controller.request_form.InterviewResultRequestForm;
import com.wowraid.jobspoon.interview.controller.response_form.InterviewResultResponseForm;
import lombok.Getter;

import java.util.List;

@Getter
public class InterviewResultResponse {

    private String userToken;
    private InterviewResultRequestForm.InterviewResultData result;
    private String status;  // "FAILED" (실패 시에만)
    private String error;   // 에러 메시지 (실패 시에만)
    private String sender;

    public InterviewResultResponse(String userToken, InterviewResultRequestForm.InterviewResultData result, String status, String error, String sender) {
        this.userToken = userToken;
        this.result = result;
        this.status = status;
        this.error = error;
        this.sender = sender;
    }

    public InterviewResultResponse() {
    }
}
