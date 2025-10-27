package com.wowraid.jobspoon.quiz.service;

import com.wowraid.jobspoon.quiz.entity.enums.JobRole;
import com.wowraid.jobspoon.quiz.entity.enums.QuizPartType;
import com.wowraid.jobspoon.quiz.repository.QuizPublicationRepository;
import com.wowraid.jobspoon.quiz.service.response.BuiltQuizSetResponse;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DailyQuizServiceImpl implements DailyQuizService {
    private final QuizPublicationRepository publicationRepository;
    private final EntityManager em;

    @Transactional(readOnly = true)
    public BuiltQuizSetResponse resolve(LocalDate date, QuizPartType part, JobRole role) {
        var pub = publicationRepository
                .findFirstByScheduledDateAndPartTypeAndJobRoleAndActiveIsTrue(date, part, role)
                .or(() -> publicationRepository
                        .findFirstByScheduledDateAndPartTypeAndJobRoleAndActiveIsTrue(date, part, JobRole.GENERAL))
                .orElseThrow(() -> new IllegalArgumentException("해당 날짜/파트 공개 세트가 없습니다."));

        Long setId = pub.getQuizSet().getId();
        List<Long> qids = em.createQuery(
                "select q.id from QuizQuestion q where q.quizSet.id = :sid order by coalesce(q.orderIndex, q.id)",
                Long.class
        ).setParameter("sid", setId).getResultList();

        return BuiltQuizSetResponse.builder()
                .quizSetId(setId)
                .questionIds(qids)
                .title(pub.getQuizSet().getTitle())
                .isRandom(pub.getQuizSet().isRandom())
                .totalQuestions(qids.size())
                .build();
    }
}