package com.wowraid.jobspoon.account_project.service;

import com.wowraid.jobspoon.account.entity.Account;
import com.wowraid.jobspoon.account_project.entity.AccountProject;
import com.wowraid.jobspoon.interview.controller.request.InterviewAccountProjectRequest;

import java.util.List;

public interface AccountProjectService {

    List<AccountProject> findAllByAccount_IdAndIsActiveTrue(Long accountId);
    void saveAllByInterviewAccountProjectRequest(List<InterviewAccountProjectRequest> interviewAccountProjectRequests, Account account);

}
