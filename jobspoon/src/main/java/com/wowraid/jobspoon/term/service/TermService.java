package com.wowraid.jobspoon.term.service;


import com.wowraid.jobspoon.term.entity.Term;
import com.wowraid.jobspoon.term.service.request.CreateTermRequest;
import com.wowraid.jobspoon.term.service.request.ListTermRequest;
import com.wowraid.jobspoon.term.service.request.UpdateTermRequest;
import com.wowraid.jobspoon.term.service.response.CreateTermResponse;
import com.wowraid.jobspoon.term.service.response.ListTermResponse;
import com.wowraid.jobspoon.term.service.response.UpdateTermResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

public interface TermService {
    CreateTermResponse register(CreateTermRequest createTermRequest);
    UpdateTermResponse updateTerm(UpdateTermRequest updateTermRequest);
    ResponseEntity<Void> deleteTerm(Long termId);
    ListTermResponse list(ListTermRequest request);

}
