package com.wowraid.jobspoon.infrastructure.external.fastapi.client;

import com.wowraid.jobspoon.infrastructure.external.fastapi.request.FastApiFourthProgressRequest;
import com.wowraid.jobspoon.infrastructure.external.fastapi.response.FastApiQuestionResponse;

public interface FastApiFifthFollowupQuestionClient {

    FastApiQuestionResponse getFastApiFifthFollowupQuestion(FastApiFourthProgressRequest request);

}
