package com.wowraid.jobspoon.quiz.service;

import com.wowraid.jobspoon.quiz.entity.QuizQuestion;
import com.wowraid.jobspoon.quiz.entity.QuizSet;
import com.wowraid.jobspoon.quiz.repository.QuizQuestionRepository;
import com.wowraid.jobspoon.quiz.repository.QuizSetRepository;
import com.wowraid.jobspoon.quiz.service.request.CreateQuizSetByCategoryRequest;
import com.wowraid.jobspoon.quiz.service.response.CreateQuizSetByCategoryResponse;
import com.wowraid.jobspoon.term.entity.Category;
import com.wowraid.jobspoon.term.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuizSetServiceImpl implements QuizSetService {

    private final CategoryRepository categoryRepository;
    private final QuizSetRepository quizSetRepository;
    private final QuizQuestionRepository quizQuestionRepository;

    @Override
    public CreateQuizSetByCategoryResponse registerQuizSetByCategory(CreateQuizSetByCategoryRequest request) {
        // categoryId로 Category 조회
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리를 찾을 수 없습니다."));

        // 퀴즈 문제 리스트 조회
        List<QuizQuestion> questions = quizSetRepository.findByCategory(category);

        // QuizSet 객체 생성
        QuizSet quizSet = new QuizSet(request.getTitle(), category, request.isRandom());

        for(QuizQuestion question : questions) {
            question.setQuizSet(quizSet);
        }

        // Repository에 저장(cascade 사용 안 하면 questions도 직접 저장 필요)
        QuizSet savedQuizSet = quizSetRepository.save(quizSet);
        quizQuestionRepository.saveAll(questions); // cascade 안 쓴 경우 필요

        return CreateQuizSetByCategoryResponse.from(savedQuizSet);
    }
}
