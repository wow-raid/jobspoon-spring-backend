package com.wowraid.jobspoon.quiz.controller.request_form;

import com.wowraid.jobspoon.quiz.service.request.SubmitAnswerRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class SubmitAnswerRequestForm {

    private List<AnswerForm> answers;

    @Getter
    @RequiredArgsConstructor
    public static class AnswerForm {
        private final Long quizQuestionId;
        private final Long selectedChoiceId;
    }

    public List<SubmitAnswerRequest> toSubmitAnswerRequests() {
        return answers.stream()
                .map(answer -> new SubmitAnswerRequest(
                        answer.getQuizQuestionId(),
                        answer.getSelectedChoiceId()
                )).toList();
    }
}
