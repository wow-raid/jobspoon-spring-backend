package com.wowraid.jobspoon.infrastructure.external.fastapi.client;

import com.wowraid.jobspoon.infrastructure.external.fastapi.response.FastApiFirstQuestionRequest;
import com.wowraid.jobspoon.infrastructure.external.fastapi.response.FastApiFirstQuestionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class FastApiFirstFollowupQuestionClient implements FastApiClient {

    private final RestTemplate restTemplate;

    @Value("${fastapi.uri}")
    private String fastApiUri;

    @Override
    public FastApiFirstQuestionResponse getFastApiFirstFollowupQuestion(FastApiFirstQuestionRequest request) {
        try {
            return restTemplate.postForObject(
                    fastApiUri + "/interview/question/first-followup-generate", // URL
                    request, // 요청 바디
                    FastApiFirstQuestionResponse.class // 응답 타입
            );
        } catch (Exception ex) {
            throw new RuntimeException("FastAPI 요청 중 오류 발생", ex);
        }
    }
}
