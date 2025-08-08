package com.wowraid.jobspoon.quiz.controller.response_form;

import com.wowraid.jobspoon.quiz.entity.UserQuizAnswer;
import com.wowraid.jobspoon.quiz.service.response.SubmitAnswerResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class SubmitAnswerResponseForm {
    private final int savedCount;      // 저장된 응답 개수
    private final int correctCount;    // 저장 처리된 응답 개수

    public static SubmitAnswerResponseForm from(List<UserQuizAnswer> answerList) {
        List<SubmitAnswerResponse> detailList = answerList.stream()
                .map(SubmitAnswerResponse::from)
                .toList();

        int savedCount = answerList.size();
        int correctCount = (int) detailList.stream().filter(SubmitAnswerResponse::isCorrect).count();
        return new SubmitAnswerResponseForm(savedCount, correctCount);
    }
}
