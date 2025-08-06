package com.wowraid.jobspoon.quiz.service;

import com.wowraid.jobspoon.account.entity.Account;
import com.wowraid.jobspoon.account.repository.AccountRepository;
import com.wowraid.jobspoon.quiz.controller.request_form.SubmitAnswerRequestForm;
import com.wowraid.jobspoon.quiz.entity.QuizChoice;
import com.wowraid.jobspoon.quiz.entity.QuizQuestion;
import com.wowraid.jobspoon.quiz.entity.UserQuizAnswer;
import com.wowraid.jobspoon.quiz.repository.QuizChoiceRepository;
import com.wowraid.jobspoon.quiz.repository.QuizQuestionRepository;
import com.wowraid.jobspoon.quiz.repository.UserQuizAnswerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserQuizAnswerServiceImpl implements UserQuizAnswerService {

    private final UserQuizAnswerRepository userQuizAnswerRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizChoiceRepository quizChoiceRepository;
    private final AccountRepository accountRepository;

    @Override
    public List<UserQuizAnswer> registerQuizResult(Long accountId, List<SubmitAnswerRequestForm> requestList) {
        List<UserQuizAnswer> results = new ArrayList<>();
        for (SubmitAnswerRequestForm submitAnswerRequestForm : requestList) {
            Account account = accountRepository.findById(accountId)
              .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

            for (SubmitAnswerRequestForm.AnswerForm answer : submitAnswerRequestForm.getAnswers()) {
                // 문제 조회
                QuizQuestion question = quizQuestionRepository.findById(answer.getQuizQuestionId())
                        .orElseThrow(() -> new IllegalArgumentException("해당 퀴즈 문제가 없습니다."));

                // 보기 조회
                QuizChoice choice = quizChoiceRepository.findById(answer.getSelectedChoiceId())
                        .orElseThrow(() -> new IllegalArgumentException("해당 보기가 존재하지 않습니다."));

                // 해당 보기가 이 문제에 속하는지 여부
                if(!choice.getQuizQuestion().getId().equals(question.getId())) {
                    throw new IllegalArgumentException("선택한 보기는 해당 문제에 속하지 않습니다.");
                }

                // 응답 생성
                UserQuizAnswer userQuizAnswer = new UserQuizAnswer(account, question, choice);
                results.add(userQuizAnswer);
            }
        }
        return userQuizAnswerRepository.saveAll(results);
    }
}
