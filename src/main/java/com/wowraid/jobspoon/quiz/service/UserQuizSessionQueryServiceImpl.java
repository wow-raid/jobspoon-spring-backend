package com.wowraid.jobspoon.quiz.service;

import com.wowraid.jobspoon.quiz.controller.response_form.SessionItemsPageResponseForm;
import com.wowraid.jobspoon.quiz.controller.response_form.SessionSummaryResponseForm;
import com.wowraid.jobspoon.quiz.entity.QuizChoice;
import com.wowraid.jobspoon.quiz.entity.QuizQuestion;
import com.wowraid.jobspoon.quiz.entity.UserQuizSession;
import com.wowraid.jobspoon.quiz.entity.enums.SessionStatus;
import com.wowraid.jobspoon.quiz.repository.QuizChoiceRepository;
import com.wowraid.jobspoon.quiz.repository.QuizQuestionRepository;
import com.wowraid.jobspoon.quiz.repository.UserQuizSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQuizSessionQueryServiceImpl implements UserQuizSessionQueryService {

    private final UserQuizSessionRepository userQuizSessionRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizChoiceRepository quizChoiceRepository;

    private static final Duration EXPIRE_AFTER = Duration.ofMinutes(60);

    /** 상태/권한/만료 전환 포함 단건 요약 */
    @Override
    @Transactional
    public SessionSummaryResponseForm getSummary(Long sessionId, Long accountId) {
        UserQuizSession userQuizsession =
                userQuizSessionRepository.findByIdAndAccount_Id(sessionId, accountId)
                        .orElseThrow(() -> new SecurityException("세션이 없거나 권한이 없습니다."));

        SessionStatus effective = ensureCurrentStatus(userQuizsession);

        return SessionSummaryResponseForm.builder()
                .sessionId(userQuizsession.getId())
                .status(effective)
                .totalCount(userQuizsession.getSnapshotQuestionIds().size())
                .lastActivityAt(userQuizsession.getLastActivityAt())
                .seedMode(userQuizsession.getSeedMode())
                .build();
    }

    /** 문항 페이지: 스냅샷 순서 기준 offset/limit */
    @Override
    @Transactional
    public SessionItemsPageResponseForm getSessionItems(Long sessionId, Long accountId, int offset, int limit) {
        UserQuizSession userQuizsession =
                userQuizSessionRepository.findByIdAndAccount_Id(sessionId, accountId)
                        .orElseThrow(() -> new SecurityException("세션이 없거나 권한이 없습니다."));

        SessionStatus effective = ensureCurrentStatus(userQuizsession);

        if (effective == SessionStatus.EXPIRED) {
            throw new IllegalStateException("만료된 세션 문제 조회는 금지됩니다.");
        }

        List<Long> allIds = userQuizsession.getSnapshotQuestionIds();
        int total = allIds.size();
        int from = Math.max(0, Math.min(offset, total));
        int to   = Math.max(from, Math.min(from + limit, total));
        List<Long> pageIds = allIds.subList(from, to);

        // 질문 배치 로딩
        Map<Long, QuizQuestion> byId = quizQuestionRepository.findAllById(pageIds)
                .stream().collect(Collectors.toMap(QuizQuestion::getId, q -> q));

        // 선택지 배치 로딩
        List<QuizChoice> allChoices = quizChoiceRepository.findByQuizQuestionIdIn(pageIds);

        // 질문별 그룹핑
        Map<Long, List<QuizChoice>> choicesByQ = allChoices.stream()
                .collect(Collectors.groupingBy(c -> c.getQuizQuestion().getId()));

        boolean revealAnswer = (effective == SessionStatus.SUBMITTED);

        List<SessionItemsPageResponseForm.Item> items = new ArrayList<>();
        for (Long qid : pageIds) {
            QuizQuestion question = byId.get(qid);
            if (question == null) continue;

            List<SessionItemsPageResponseForm.Choice> choiceList =
                    choicesByQ.getOrDefault(qid, List.of()).stream()
                            .map(c -> SessionItemsPageResponseForm.Choice.builder()
                                    .id(c.getId())
                                    .text(c.getChoiceText())
                                    .isAnswer(revealAnswer ? c.isAnswer() : null)
                                    .build())
                            .toList();

            items.add(SessionItemsPageResponseForm.Item.builder()
                    .questionId(qid)
                    .questionText(question.getQuestionText())
                    .choices(choiceList)
                    .build());
        }

        return SessionItemsPageResponseForm.builder()
                .sessionId(userQuizsession.getId())
                .offset(offset)
                .limit(limit)
                .total(total)
                .items(items)
                .build();
    }

    /** 마지막 활동 60분 초과 시 EXPIRE 전환(상태 계산 및 필요 시 DB 전환) */
    @Transactional
    protected SessionStatus ensureCurrentStatus(UserQuizSession userQuizSession) {
        SessionStatus current = userQuizSession.getSessionStatus();
        if (current == SessionStatus.SUBMITTED) return current;

        Instant last = Optional.ofNullable(userQuizSession.getLastActivityAt()).orElse(Instant.EPOCH);
        if (last.plus(EXPIRE_AFTER).isBefore(Instant.now())) {
            // DB 상태만 EXPIRED로 전환 (세션이 SUBMITTED가 아니면)
            userQuizSessionRepository.expireIfNotSubmitted(userQuizSession.getId());
            return SessionStatus.EXPIRED;
        }
        return current;
    }
}
