package com.wowraid.jobspoon.quiz.service;

import com.wowraid.jobspoon.quiz.entity.QuizQuestion;
import com.wowraid.jobspoon.quiz.entity.QuizSet;
import com.wowraid.jobspoon.quiz.repository.QuizQuestionRepository;
import com.wowraid.jobspoon.quiz.repository.QuizSetRepository;
import com.wowraid.jobspoon.quiz.service.generator.AutoQuizGenerator;
import com.wowraid.jobspoon.quiz.service.request.CreateQuizSessionRequest;
import com.wowraid.jobspoon.quiz.service.request.CreateQuizSetByCategoryRequest;
import com.wowraid.jobspoon.quiz.service.response.CreateQuizSessionResponse;
import com.wowraid.jobspoon.quiz.service.response.CreateQuizSetByCategoryResponse;
import com.wowraid.jobspoon.term.entity.Category;
import com.wowraid.jobspoon.term.entity.Term;
import com.wowraid.jobspoon.term.repository.CategoryRepository;
import com.wowraid.jobspoon.user_term.repository.FavoriteTermRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuizSetServiceImpl implements QuizSetService {

    private final CategoryRepository categoryRepository;
    private final QuizSetRepository quizSetRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final FavoriteTermRepository favoriteTermRepository;
    private final AutoQuizGenerator autoQuizGenerator;

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

    @Override
    @Transactional
    public CreateQuizSessionResponse registerQuizSetByFavorites(CreateQuizSessionRequest request) {
        Long folderId = request.getFolderId();
        List<Term> terms = (folderId == null)
                ? favoriteTermRepository.findTermsByAccount(request.getAccountId())
                : favoriteTermRepository.findTermsByAccountAndFolder(request.getAccountId(), folderId);

        if (terms.isEmpty()) {
            throw new IllegalArgumentException("즐겨찾기 용어가 없습니다.");
        }

        String seedMode   = request.getSeedMode()   == null ? "AUTO"   : request.getSeedMode();
        String difficulty = request.getDifficulty() == null ? "MEDIUM" : request.getDifficulty();

        // 1) 문항만 생성
        List<QuizQuestion> questions = autoQuizGenerator.generateQuestions(
                terms, request.getQuestionTypes(), request.getCount(),
                request.getMcqEach(), request.getOxEach(), request.getInitialsEach(),
                seedMode, difficulty
        );

        if (questions.isEmpty()) throw new IllegalStateException("생성된 문제가 없습니다.");

        // 2) 세트 저장 + 문항 저장(Managed 상태로)
        QuizSet set = quizSetRepository.save(new QuizSet("[GEN] Favorites", true));
        questions.forEach(q -> q.setQuizSet(set));
        quizQuestionRepository.saveAll(questions);

        // 3) 보기 생성·저장 (이제 q는 Managed)
        autoQuizGenerator.createAndSaveChoicesFor(questions);

        return CreateQuizSessionResponse.of(
                set.getId(),
                questions.stream().map(QuizQuestion::getId).toList()
        );
    }
}
