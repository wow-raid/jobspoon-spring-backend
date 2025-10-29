package com.wowraid.jobspoon.interview.service;

import com.wowraid.jobspoon.interview.controller.response_form.UserTechStackResponse;

public interface UserTechStackService {
    UserTechStackResponse getUserTechStack(Long accountId);
}
