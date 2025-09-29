package com.wowraid.jobspoon.quiz.service;
import com.wowraid.jobspoon.account.entity.Account;
import com.wowraid.jobspoon.account.repository.AccountRepository;
import com.wowraid.jobspoon.quiz.entity.*;
import com.wowraid.jobspoon.quiz.entity.enums.SessionMode;
import com.wowraid.jobspoon.quiz.repository.*;
import com.wowraid.jobspoon.quiz.service.response.StartUserQuizSessionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserQuizAnswerServiceImpl implements UserQuizAnswerService {

    private final AccountRepository accountRepository;
    private final UserQuizSessionRepository userQuizSessionRepository;
    private final QuizSetRepository quizSetRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizChoiceRepository quizChoiceRepository;

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

    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
    private String toJson(List<Long> ids) {
        try { return objectMapper.writeValueAsString(ids); }
        catch (Exception e) { throw new IllegalStateException("Failed to serialize questionIds", e); }
    }

//    @Override
//    public List<UserQuizAnswer> registerQuizResult(Long accountId, List<SubmitAnswerRequestForm> requestList) {
//        List<UserQuizAnswer> results = new ArrayList<>();
//
//        Account account = accountRepository.findById(accountId)
//                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));
//
//        for (SubmitAnswerRequestForm submitAnswerRequestForm : requestList) {
//            for (SubmitAnswerRequestForm.AnswerForm answer : submitAnswerRequestForm.getAnswers()) {
//
//                // 문제 조회
//                QuizQuestion question = quizQuestionRepository.findById(answer.getQuizQuestionId())
//                        .orElseThrow(() -> new IllegalArgumentException("해당 퀴즈 문제가 없습니다."));
//
//                // 보기 조회
//                QuizChoice choice = quizChoiceRepository.findById(answer.getSelectedChoiceId())
//                        .orElseThrow(() -> new IllegalArgumentException("해당 보기가 존재하지 않습니다."));
//
//                // 해당 보기가 이 문제에 속하는지 여부
//                if(!choice.getQuizQuestion().getId().equals(question.getId())) {
//                    throw new IllegalArgumentException("선택한 보기는 해당 문제에 속하지 않습니다.");
//                }
//
//                // 응답 생성
//                UserQuizAnswer userQuizAnswer = new UserQuizAnswer(account, question, choice);
//                results.add(userQuizAnswer);
//            }
//        }
//        List<UserQuizAnswer> savedAnswers = userQuizAnswerRepository.saveAll(results);
//        saveWrongNotes(savedAnswers, account.getId());
//        return savedAnswers;
//    }
//
//    public void saveWrongNotes(List<UserQuizAnswer> answers, Long accountId) {
//        List<UserWrongNote> wrongNotesToSave = answers.stream()
//                .filter(answer -> !answer.isCorrect())
//                .filter(answer -> !userWrongNoteRepository.existsByAccountIdAndQuizQuestionId(
//                        accountId, answer.getQuizQuestion().getId()))
//                .map(answer -> UserWrongNote.builder()
//                        .account(answer.getAccount())
//                        .quizQuestion(answer.getQuizQuestion())
//                        .quizChoice(answer.getQuizChoice())
//                        .submittedAt(LocalDateTime.now())
//                        .explanation(answer.getQuizChoice().getExplanation())
//                        .build())
//                .toList();
//
//        userWrongNoteRepository.saveAll(wrongNotesToSave);
//    }

}
