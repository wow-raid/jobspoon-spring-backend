package com.wowraid.jobspoon.infrastructure.external.fastapi.client;

import com.wowraid.jobspoon.infrastructure.external.fastapi.response.FastApiFirstQuestionRequest;
import com.wowraid.jobspoon.infrastructure.external.fastapi.response.FastApiFirstQuestionResponse;

public interface FastApiClient {

    FastApiFirstQuestionResponse getFastApiFirstFollowupQuestion(FastApiFirstQuestionRequest request);

}
