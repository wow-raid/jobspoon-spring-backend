package com.wowraid.jobspoon.user_term.service;

import com.wowraid.jobspoon.user_term.service.request.RecordTermViewRequest;
import com.wowraid.jobspoon.user_term.service.response.RecordTermViewResponse;

public interface UserRecentTermService {
    RecordTermViewResponse recordTermView(RecordTermViewRequest request);
}
