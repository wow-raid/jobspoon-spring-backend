package com.wowraid.jobspoon.infrastructure.external.fastapi.client;

import com.wowraid.jobspoon.infrastructure.external.fastapi.request.FastApiFourthProgressRequest;
import com.wowraid.jobspoon.infrastructure.external.fastapi.request.FastApiThirdProgressRequest;
import com.wowraid.jobspoon.infrastructure.external.fastapi.response.FastApiQuestionResponse;

public interface FastApiFourthFollowupQuestionClient {

    FastApiQuestionResponse getFastApiFourthFollowupQuestion(FastApiFourthProgressRequest request);

}
