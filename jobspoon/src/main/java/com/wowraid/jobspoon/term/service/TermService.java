package com.wowraid.jobspoon.term.service;


import com.wowraid.jobspoon.term.service.request.CreateTermRequest;
import com.wowraid.jobspoon.term.service.response.CreateTermResponse;

public interface TermService {
    CreateTermResponse register(CreateTermRequest createTermRequest);

}
