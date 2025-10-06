package com.wowraid.jobspoon.infrastructure.external.fastapi.client;

import com.wowraid.jobspoon.infrastructure.external.fastapi.request.FastApiSecondProgressRequest;
import com.wowraid.jobspoon.infrastructure.external.fastapi.request.FastApiThirdProgressRequest;
import com.wowraid.jobspoon.infrastructure.external.fastapi.response.FastApiQuestionResponse;


public interface FastApiThirdFollowupQuestionClient {

    FastApiQuestionResponse getFastApiThirdFollowupQuestion(FastApiThirdProgressRequest request);


}
