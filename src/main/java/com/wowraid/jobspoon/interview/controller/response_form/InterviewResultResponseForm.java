package com.wowraid.jobspoon.interview.controller.response_form;

import com.wowraid.jobspoon.interview.controller.request_form.InterviewResultRequestForm;
import lombok.Getter;

@Getter
public class InterviewResultResponseForm {

    private String userToken;
    private InterviewResultRequestForm.InterviewResultData result;
    private String status;  // "FAILED" (실패 시에만)
    private String error;   // 에러 메시지 (실패 시에만)


    public InterviewResultResponseForm(String userToken, InterviewResultRequestForm.InterviewResultData result, String status, String error) {
        this.userToken = userToken;
        this.result = result;
        this.status = status;
        this.error = error;
    }

    public InterviewResultResponseForm() {
    }
}
