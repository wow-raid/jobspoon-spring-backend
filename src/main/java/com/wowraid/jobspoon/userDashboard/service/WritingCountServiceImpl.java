package com.wowraid.jobspoon.userDashboard.service;

import com.wowraid.jobspoon.studyroom.repository.StudyRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WritingCountServiceImpl implements WritingCountService {

    private final StudyRoomRepository studyRoomRepository;
    // TODO: ReviewRepository, CommentRepository 도메인 생기면 주입

    @Override
    public long getStudyroomsCount(Long accountId) {
        return studyRoomRepository.countByHost_Account_Id(accountId);
    }
}
