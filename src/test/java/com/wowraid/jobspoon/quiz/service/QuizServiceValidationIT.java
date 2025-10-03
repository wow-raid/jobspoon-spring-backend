package com.wowraid.jobspoon.quiz.service;

import com.wowraid.jobspoon.account.entity.Account;
import com.wowraid.jobspoon.account.repository.AccountRepository;
import com.wowraid.jobspoon.quiz.entity.enums.QuestionType;
import com.wowraid.jobspoon.quiz.entity.enums.SeedMode;
import com.wowraid.jobspoon.quiz.repository.QuizQuestionRepository;
import com.wowraid.jobspoon.quiz.service.request.CreateQuizSessionRequest;
import com.wowraid.jobspoon.term.entity.Category;
import com.wowraid.jobspoon.term.entity.Term;
import com.wowraid.jobspoon.term.repository.CategoryRepository;
import com.wowraid.jobspoon.term.repository.TermRepository;
import com.wowraid.jobspoon.user_term.entity.FavoriteTerm;
import com.wowraid.jobspoon.user_term.entity.UserWordbookFolder;
import com.wowraid.jobspoon.user_term.repository.FavoriteTermRepository;
import com.wowraid.jobspoon.user_term.repository.UserWordbookFolderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class QuizServiceValidationIT {

    @Autowired QuizQuestionRepository quizQuestionRepository;
    @Autowired QuizSetService quizSetService;
    @Autowired AccountRepository accountRepository;
    @Autowired CategoryRepository categoryRepository;
    @Autowired UserWordbookFolderRepository userWordbookFolderRepository;
    @Autowired FavoriteTermRepository favoriteTermRepository;
    @Autowired TermRepository termRepository;

    // 테스트용 임의 계정 ID 조회
    private long anyAccountId() {
        return accountRepository.findAll().stream()
                .findFirst().orElseThrow().getId();
    }

    // questionTypes가 null이거나 빈 리스트면 예외가 발생하는지 테스트
    @Test
    void questionTypes_null_or_empty_shouldFail() {
        long acc = anyAccountId();

        // null
        var req1 = CreateQuizSessionRequest.builder()
                .accountId(acc)
                .questionTypes(null)
                .count(5)
                .seedMode(SeedMode.FIXED)
                .difficulty("MEDIUM")
                .build();
        assertThrows(IllegalArgumentException.class, () -> quizSetService.registerQuizSetByFavorites(req1));

        // empty
        var req2 = CreateQuizSessionRequest.builder()
                .accountId(acc)
                .questionTypes(java.util.List.of())
                .count(5)
                .seedMode(SeedMode.FIXED)
                .difficulty("MEDIUM")
                .build();
        assertThrows(IllegalArgumentException.class, () -> quizSetService.registerQuizSetByFavorites(req2));
    }

    // 문제 개수(count)가 0이하이면 예외가 발생하는지 테스트
    @Test
    void count_invalid_shouldFailOrClamp() {
        long acc = anyAccountId();

        var reqZero = CreateQuizSessionRequest.builder()
                .accountId(acc)
                .questionTypes(java.util.List.of(com.wowraid.jobspoon.quiz.entity.enums.QuestionType.CHOICE))
                .count(0)
                .seedMode(SeedMode.FIXED)
                .difficulty("MEDIUM")
                .build();
        assertThrows(IllegalArgumentException.class, () -> quizSetService.registerQuizSetByFavorites(reqZero));

        var reqNegative = CreateQuizSessionRequest.builder()
                .accountId(acc)
                .questionTypes(java.util.List.of(com.wowraid.jobspoon.quiz.entity.enums.QuestionType.CHOICE))
                .count(-3)
                .seedMode(SeedMode.FIXED)
                .difficulty("MEDIUM")
                .build();
        assertThrows(IllegalArgumentException.class, () -> quizSetService.registerQuizSetByFavorites(reqNegative));
    }

    // 알 수 없는 난이도 입력 시 기본값(MEDIUM)으로 동작하는지 테스트
    @Test
    void difficulty_unknown_defaultsToMedium() {
        long accId = anyAccountId();
        ensureOneFavoriteFor(accId); // 즐겨찾기 최소 1개 보장

        var req = CreateQuizSessionRequest.builder()
                .accountId(accId)
                .questionTypes(List.of(QuestionType.INITIALS))
                .count(1)
                .seedMode(SeedMode.DAILY)
                .difficulty("WTF_IS_THIS")
                .build();

        var set = quizSetService.registerQuizSetByFavorites(req);
        Long firstQid = set.getQuestionIds().get(0);
        var q = quizQuestionRepository.findById(firstQid).orElseThrow();

        String stem = q.getQuestionText();
        assertTrue(stem.contains("글자수") || stem.contains("글자 수"));
        assertTrue(stem.contains("초성"));
    }

    // --- helpers -------------------------------------------------------------

    // 테스트용 계정에 최소 1개의 즐겨찾기 용어를 생성하는 헬퍼 메서드
    private void ensureOneFavoriteFor(long accountId) {
        Account acc = accountRepository.findById(accountId).orElseThrow();

        // 카테고리 생성
        Category cat = Category.builder()
                .type("GENERAL")
                .groupName("DEFAULT_GROUP")
                .name("TEST-" + System.nanoTime())
                .depth(1)
                .sortOrder(0)
                .parent(null)
                .build();
        cat = categoryRepository.save(cat);

        // 폴더 생성 (필드 접근자가 없으면 리플렉션)
        UserWordbookFolder folder = new UserWordbookFolder();
        try {
            var fAcc = folder.getClass().getDeclaredField("account");
            fAcc.setAccessible(true);
            fAcc.set(folder, acc);
            var fName = folder.getClass().getDeclaredField("folderName");
            fName.setAccessible(true);
            fName.set(folder, "VAL-TEST-" + accountId + "-" + System.nanoTime());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        folder = userWordbookFolderRepository.save(folder);

        // 용어 + 즐겨찾기
        Term term = new Term();
        term.setTitle("미디엄기본검증");
        term.setDescription("설명");
        term.setCategory(cat);
        term = termRepository.save(term);

        favoriteTermRepository.save(new FavoriteTerm(null, acc, term, folder, null));
    }
}
