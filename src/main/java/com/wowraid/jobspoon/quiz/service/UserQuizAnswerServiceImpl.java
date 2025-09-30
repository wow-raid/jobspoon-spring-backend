package com.wowraid.jobspoon.quiz.service;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wowraid.jobspoon.account.entity.Account;
import com.wowraid.jobspoon.account.repository.AccountRepository;
import com.wowraid.jobspoon.quiz.controller.request_form.SubmitQuizSessionRequestForm;
import com.wowraid.jobspoon.quiz.controller.response_form.SubmitQuizSessionResponseForm;
import com.wowraid.jobspoon.quiz.entity.*;
import com.wowraid.jobspoon.quiz.entity.enums.SessionMode;
import com.wowraid.jobspoon.quiz.repository.*;
import com.wowraid.jobspoon.quiz.service.response.StartUserQuizSessionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserQuizAnswerServiceImpl implements UserQuizAnswerService {

    private final AccountRepository accountRepository;
    private final UserQuizSessionRepository userQuizSessionRepository;
    private final QuizSetRepository quizSetRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizChoiceRepository quizChoiceRepository;
    private final UserWrongNoteRepository userWrongNoteRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SessionAnswerRepository sessionAnswerRepository;

    @Override
    @Transactional
    public StartUserQuizSessionResponse startFromQuizSet(Long accountId, Long quizSetId, List<Long> questionIds) {
        Account account = accountRepository.getReferenceById(accountId);
        QuizSet quizSet = quizSetRepository.getReferenceById(quizSetId);

        UserQuizSession session = new UserQuizSession();
        // 세션 시작
        session.begin(
                account,
                quizSet,
                SessionMode.FULL,
                1,
                questionIds.size(),
                toJson(questionIds)
        );

        userQuizSessionRepository.save(session);

        // 미리보기용 아이템 구성
        // 세션을 만들자마자 바로 풀 수 있도록 서버가 응답에 문항 목록(문제 본문 + 보기 텍스트)을 함께 실어주는 것
        List<QuizQuestion> questions = quizQuestionRepository.findAllById(questionIds);

        // 질문 순서 보존
        Map<Long, QuizQuestion> qMap = questions.stream()
                .collect(Collectors.toMap(QuizQuestion::getId, q -> q, (a,b)->a, LinkedHashMap::new));

        // 보기 배치 조회
        var allChoices = quizChoiceRepository.findByQuizQuestionIdIn(questionIds);
        Map<Long, List<QuizChoice>> byQ = allChoices.stream()
                .collect(Collectors.groupingBy(c -> c.getQuizQuestion().getId()));

        List<StartUserQuizSessionResponse.Item> items = questionIds.stream()
                .map(qid -> {
                    QuizQuestion q = qMap.get(qid);
                    var options = byQ.getOrDefault(qid, List.of()).stream()
                            .map(c -> new StartUserQuizSessionResponse.Option(c.getId(), c.getChoiceText()))
                            .toList();
                    return new StartUserQuizSessionResponse.Item(
                            q.getId(),
                            q.getQuestionType(),
                            q.getQuestionText(),
                            options
                    );
                })
                .toList();
        return new StartUserQuizSessionResponse(session.getId(), items);
    }

    @Override
    public SubmitQuizSessionResponseForm submitSession(Long sessionId, Long accountId, SubmitQuizSessionRequestForm requestForm) {

        // 1) 세션 로드 + 소유자/상태 검증
        UserQuizSession session = userQuizSessionRepository.findById(sessionId).orElseThrow(()-> new IllegalArgumentException("세션을 찾을 수 없습니다."));
        if (!session.getAccount().getId().equals(accountId)) {
            throw new SecurityException("세션 접근 권한이 없습니다.");
        }
        if (session.getSubmittedAt() != null) {
            throw new IllegalStateException("이미 제출된 세션입니다.");
        }

        // 2) 스냅샷과 제출 문항 매칭(세션 외부의 문제/보기 제출 차단)
        Set<Long> snapshotQids = parseSnapshotIds(session.getQuestionsSnapshotJson());
        List<Long> submittedQids = requestForm.getAnswers().stream().map(SubmitQuizSessionRequestForm.AnswerForm::getQuizQuestionId).toList();
        if (!snapshotQids.containsAll(submittedQids)) {
            throw new IllegalArgumentException("세션에 속하지 않는 문제가 포함되어 있습니다.");
        }

        // 3) 배치 조회
        List<Long> choiceIds = requestForm.getAnswers().stream().map(SubmitQuizSessionRequestForm.AnswerForm::getSelectredChoiceId).toList();
        Map<Long, QuizQuestion> qMap = quizQuestionRepository.findAllById(submittedQids)
                .stream().collect(Collectors.toMap(QuizQuestion::getId, q -> q));

        Map<Long, QuizChoice> cMap = quizChoiceRepository.findAllById(choiceIds)
                .stream().collect(Collectors.toMap(QuizChoice::getId, c -> c));

        // 4) 채점 + SessionAnswer 생성
        int correct = 0;
        LocalDateTime now = LocalDateTime.now();
        List<SessionAnswer> toSave = new ArrayList<>();
        List<SubmitQuizSessionResponseForm.Item> details = new ArrayList<>();

        for (var a : requestForm.getAnswers()) {
            QuizQuestion q = Optional.ofNullable(qMap.get(a.getQuizQuestionId()))
                    .orElseThrow(()-> new IllegalArgumentException("유효하지 않은 문제입니다: " + a.getQuizQuestionId()));

            QuizChoice c = Optional.ofNullable(cMap.get(a.getSelectredChoiceId()))
                    .orElseThrow(()-> new IllegalArgumentException("유효하지 않은 보기입니다: " + a.getSelectredChoiceId()));

            if (!c.getQuizQuestion().getId().equals(q.getId())) {
                throw new IllegalArgumentException("선택한 보기는 해당 문제의 보기가 아닙니다.");
            }

            boolean isCorrect = c.isAnswer();
            if (isCorrect) {
                correct++;
            }

            toSave.add(new SessionAnswer(session, q, c, now, isCorrect));
            details.add(new SubmitQuizSessionResponseForm.Item(q.getId(), c.getId(), isCorrect));
        }

        // 5) 벌크 저장 + 세션 상태 업데이트(소요시간 처리)
        sessionAnswerRepository.saveAll(toSave);

        Long elapsedMs = requestForm.getElaspedMs();
        if (elapsedMs == null && session.getStartedAt() != null) {
            elapsedMs = Duration.between(session.getStartedAt(), now).toMillis();
        }

        session.submit(correct, elapsedMs);
        userQuizSessionRepository.save(session);

        // 6) 오답노트 저장(중복 방지)
        saveWrongNotes(toSave, accountId);

        return new SubmitQuizSessionResponseForm(session.getId(),
            session.getTotal() == null ? toSave.size() : session.getTotal(),
            correct,
            elapsedMs,
            details);
    }

    @Override
    public void saveWrongNotes(List<SessionAnswer> answers, Long accountId) {
        List<UserWrongNote> notes = answers.stream()
                .filter(a -> !a.isCorrect())
                .filter(a -> !userWrongNoteRepository.existsByAccountIdAndQuizQuestionId(accountId, a.getQuizQuestion().getId()))
                .map( a -> UserWrongNote.builder()
                        .account(a.getUserQuizSession().getAccount())
                        .quizQuestion(a.getQuizQuestion())
                        .quizChoice(a.getQuizChoice())
                        .submittedAt(a.getSubmittedAt())
                        .explanation(a.getQuizChoice().getExplanation())
                        .build())
                .toList();

        if (!notes.isEmpty()) {
            userWrongNoteRepository.saveAll(notes);
        }
    }

    private String toJson(List<Long> ids) {
        try { return objectMapper.writeValueAsString(ids); }
        catch (Exception e) { throw new IllegalStateException("Failed to serialize questionIds", e); }
    }

    private Set<Long> parseSnapshotIds(String json) {
        if (json == null || json.isBlank()) return Collections.emptySet();
        try {
            List<Long> ids = objectMapper.readValue(json, new TypeReference<List<Long>>() {});
            return new HashSet<>(ids);
        } catch (Exception e) {
            log.warn("세션 스냅샷 파싱 실패: {}", e.getMessage());
            return Collections.emptySet();
        }
    }
}
