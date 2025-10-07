package com.wowraid.jobspoon.infrastructure.external.fastapi.client;

import com.wowraid.jobspoon.infrastructure.external.fastapi.request.FastApiFirstQuestionRequest;
import com.wowraid.jobspoon.infrastructure.external.fastapi.request.FastApiSecondProgressRequest;
import com.wowraid.jobspoon.infrastructure.external.fastapi.response.FastApiQuestionResponse;


public interface FastApiSecondFollowupQuestionClient {

    FastApiQuestionResponse getFastApiSecondFollowupQuestion(FastApiSecondProgressRequest request);

}
