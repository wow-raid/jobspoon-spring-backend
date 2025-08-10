package com.wowraid.jobspoon.user_term.service;

import com.wowraid.jobspoon.user_term.repository.UserWordbookFolderRepository;
import com.wowraid.jobspoon.user_term.service.request.CreateUserWordbookFolderRequest;
import com.wowraid.jobspoon.user_term.service.response.CreateUserWordbookFolderResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserWordbookFolderServiceImpl implements UserWordbookFolderService {

    private final UserWordbookFolderRepository userWordbookFolderRepository;

    @Override
    @Transactional
    public CreateUserWordbookFolderResponse registerWordbookFolder(CreateUserWordbookFolderRequest request) {

//        Long accountId = request.getAccountId();
//        if (folderRepository.existsByAccountIdAndFolderName(accountId, request.getFolderName())) {
//            throw new IllegalStateException("이미 존재하는 폴더명입니다.");
//        }

        int max = userWordbookFolderRepository.findGlobalMaxSortOrder();
        int nextOrder = max + 1;

        var saved = userWordbookFolderRepository.save(request.toUserWordbookFolder(nextOrder));
        return CreateUserWordbookFolderResponse.from(saved);
    }
}
