package com.wowraid.jobspoon.quiz.entity;

import com.wowraid.jobspoon.account.entity.Account;
import com.wowraid.jobspoon.quiz.entity.enums.SeedMode;
import com.wowraid.jobspoon.quiz.entity.enums.SessionMode;
import com.wowraid.jobspoon.quiz.entity.enums.SessionStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
/**
 * UserQuizSession
 *
 * 사용자가 특정 퀴즈 세트를 푸는 한 번의 세션을 나타내는 엔티티.
 * - 사용자 × 퀴즈 세트 응시 이력 저장
 * - 세트 단위로 진행 상태(IN_PROGRESS, SUBMITTED, EXPIRED)와 점수를 기록한다.
 * - FULL 모드 또는 WRONG_ONLY 모드로 세션을 시작할 수 있다.
 * - parentSession을 통해 WRONG_ONLY 세션은 원본 세션과 연결된다.
 * - questionsSnapshotJson에 실제 출제된 문제 ID 스냅샷을 저장한다.
 */
@Entity
@Getter
@NoArgsConstructor
@Table(
    name = "user_quiz_session",
    indexes = {
            @Index(name = "idx_uqs_user_set", columnList = "account_id, quiz_set_id, started_at"),
            @Index(name = "idx_uqs_started", columnList = "started_at")
    }
)
public class UserQuizSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    // 세션 ID

    @Setter(AccessLevel.PACKAGE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;    // 응시 사용자

    @Setter(AccessLevel.PACKAGE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_set_id", nullable = false)
    private QuizSet quizSet;    // 푼 퀴즈 세트

    /** WRONG_ONLY일 때 원본(전체) 세션을 가리킴 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_session_id")
    private UserQuizSession parentSession;

    @Enumerated(EnumType.STRING)
    @Column(name = "session_mode", nullable = false, length = 20)
    private SessionMode sessionMode;    // 세션 모드(FULL, WRONG_ONLY)

    @Enumerated(EnumType.STRING)
    @Column(name = "session_status", nullable = false, length = 20)
    private SessionStatus sessionStatus;    // 세션 진행 상태(IN_PROGRESS, SUBMITTED, EXPIRED)

    @Column(name = "attempt_no", nullable = false)
    private Integer attemptNo;    // 해당 세트 기준 몇 번째 응시인지 (1, 2, 3…)

    /** WRONG_ONLY 같은 동적 세트의 문제 스냅샷(JSON: [qId1, qId2, ...]) */
    @Column(name = "questions_snapshot_json", columnDefinition = "TEXT")
    private String questionsSnapshotJson;

    @Column(name = "score")
    private Integer score; // 맞힌 개수(분모는 total 또는 snapshot 길이)

    @Column(name = "total")
    private Integer total; // 총 문항 수

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "elapsed_ms")
    private Long elapsedMs; // 제출까지 걸린 시간(ms), null 허용

    @Enumerated(EnumType.STRING)
    @Column(name = "seed_mode", length = 20)
    private SeedMode seedMode;  // AUTO | DAILY | FIXED

    @Column(name ="seed_value")
    private Long seed; // 최종 해석된 시드 값

    public void start(SessionMode sessionMode, SessionStatus sessionStatus, Integer attemptNo, LocalDateTime startedAt, Integer total, String questionsSnapshotJson) {
        this.sessionMode = sessionMode;
        this.sessionStatus = sessionStatus.IN_PROGRESS;
        this.attemptNo = attemptNo;
        this.startedAt = LocalDateTime.now();
        this.total = total;
        this.questionsSnapshotJson = questionsSnapshotJson;
    }

    public void submit(int finalScore) {
        this.sessionStatus = sessionStatus.SUBMITTED;
        this.submittedAt = LocalDateTime.now();
        this.score = finalScore;
    }

    public void submit(int finalScore, Long elapsedMs) {
        submit(finalScore);
        this.elapsedMs = elapsedMs;
    }

    public void expire() {
        this.sessionStatus = SessionStatus.EXPIRED;
    }

    public void start(SessionMode sessionMode, Integer attemptNo, Integer total, String questionsSnapshotJson) {
        this.start(sessionMode, SessionStatus.IN_PROGRESS, attemptNo, LocalDateTime.now(), total, questionsSnapshotJson);
    }

    public void begin(Account account, QuizSet quizSet,
                      SessionMode sessionMode, int attemptNo,
                      int total, String questionsSnapshotJson) {
        this.account = account;        // 내부에서만 세팅하므로 패키지 제한 무관
        this.quizSet = quizSet;
        this.sessionMode = sessionMode;
        this.sessionStatus = SessionStatus.IN_PROGRESS;
        this.attemptNo = attemptNo;
        this.startedAt = LocalDateTime.now();
        this.total = total;
        this.questionsSnapshotJson = questionsSnapshotJson;
    }

    // 시드까지 함께 세팅하는 시작 메서드
    public void begin(Account account,
                      QuizSet quizSet,
                      SessionMode sessionMode,
                      int attemptNo,
                      int total,
                      String questionsSnapshotJson,
                      SeedMode seedMode,
                      Long seed) {
        this.account = account;
        this.quizSet = quizSet;
        this.sessionMode = sessionMode;
        this.sessionStatus = SessionStatus.IN_PROGRESS;
        this.attemptNo = attemptNo;
        this.startedAt = LocalDateTime.now();
        this.total = total;
        this.questionsSnapshotJson = questionsSnapshotJson;

        // 시드 기록
        this.seedMode = seedMode;
        this.seed = seed;
    }

    public void beginWithParent(Account account, QuizSet quizSet, UserQuizSession parent, SessionMode mode, Integer attemptNo, Integer total, String snapshotJson) {
        this.account = account;
        this.quizSet = quizSet;
        this.parentSession = parent;
        this.sessionMode = mode;
        this.sessionStatus = SessionStatus.IN_PROGRESS;
        this.attemptNo = attemptNo;
        this.startedAt = LocalDateTime.now();
        this.total = total;
        this.questionsSnapshotJson = snapshotJson;
    }
}
