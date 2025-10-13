package com.wowraid.jobspoon.infrastructure.external.fastapi.client;

import com.wowraid.jobspoon.interview.controller.request.InterviewEndRequest;
import com.wowraid.jobspoon.interview.controller.request_form.InterviewEndRequestForm;
import com.wowraid.jobspoon.interview.service.response.InterviewCreateResponse;

public interface FastApiEndInterview {

     void endInterview(InterviewEndRequest request);

}
