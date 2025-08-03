package com.wowraid.jobspoon.quiz.controller;

import com.wowraid.jobspoon.quiz.controller.request_form.CreateQuizQuestionRequestForm;
import com.wowraid.jobspoon.quiz.controller.request_form.CreateQuizSetByCategoryRequestForm;
import com.wowraid.jobspoon.quiz.controller.response_form.CreateQuizChoiceListResponseForm;
import com.wowraid.jobspoon.quiz.controller.response_form.CreateQuizChoiceResponseForm;
import com.wowraid.jobspoon.quiz.controller.response_form.CreateQuizQuestionResponseForm;
import com.wowraid.jobspoon.quiz.controller.response_form.CreateQuizSetByCategoryResponseForm;
import com.wowraid.jobspoon.quiz.entity.QuizChoice;
import com.wowraid.jobspoon.quiz.service.QuizChoiceService;
import com.wowraid.jobspoon.quiz.service.QuizQuestionService;
import com.wowraid.jobspoon.quiz.service.QuizSetService;
import com.wowraid.jobspoon.quiz.service.request.CreateQuizChoiceRequest;
import com.wowraid.jobspoon.quiz.service.request.CreateQuizQuestionRequest;
import com.wowraid.jobspoon.quiz.service.request.CreateQuizSetByCategoryRequest;
import com.wowraid.jobspoon.quiz.service.response.CreateQuizQuestionResponse;
import com.wowraid.jobspoon.quiz.service.response.CreateQuizSetByCategoryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RestControllerAdvice
@RequestMapping("/api/quizzies")
public class QuizController {

    @ExceptionHandler
    public ResponseEntity<String> handleAll(Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    private final QuizQuestionService quizQuestionService;
    private final QuizSetService quizSetService;
    private final QuizChoiceService quizChoiceService;

    // 용어 기반 퀴즈 문제 등록하기
    @PostMapping("/{termId}/questions")
    public CreateQuizQuestionResponseForm responseForm (
            @PathVariable("termId") Long termId,
            @RequestBody CreateQuizQuestionRequestForm requestForm) {
        log.info("용어에 대한 퀴즈 문제를 등록합니다. termId: {}", termId);
        CreateQuizQuestionRequest request = requestForm.toCreateQuizQuestionRequest(termId);
        CreateQuizQuestionResponse response = quizQuestionService.registerQuizQuestion(request);
        return CreateQuizQuestionResponseForm.from(response);
    }

    // 카테고리 기반 퀴즈 세트 자동 생성
    @PostMapping("/category")
    public CreateQuizSetByCategoryResponseForm createByCategory(
            @RequestBody CreateQuizSetByCategoryRequestForm requestForm) {
        log.info("카테고리 기반으로 퀴즈 세트를 구성합니다.");
        CreateQuizSetByCategoryRequest request = requestForm.toCategoryBasedRequest();
        CreateQuizSetByCategoryResponse response = quizSetService.registerQuizSetByCategory(request);
        return CreateQuizSetByCategoryResponseForm.from(response);
    }

    // 퀴즈 보기 생성
    @PostMapping("/{quizQuestionId}/choices")
    public CreateQuizChoiceListResponseForm responseForm(
            @PathVariable("quizQuestionId") Long quizQuestionId,
            @RequestBody List<CreateQuizChoiceRequest> requestList) {
        log.info("퀴즈 문제에 대한 보기를 생성합니다. quizQuestionId: {}", quizQuestionId);
        List<QuizChoice> savedChoices = quizChoiceService.registerQuizChoices(quizQuestionId, requestList);
        return CreateQuizChoiceListResponseForm.from(savedChoices);
    }

}
