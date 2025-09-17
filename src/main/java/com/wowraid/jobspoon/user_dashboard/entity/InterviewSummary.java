package com.wowraid.jobspoon.user_dashboard.entity;

import com.wowraid.jobspoon.account.entity.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/*
장고 interview 테이블 읽는 매핑용 객체
장고에서 이미 만든 DB를 스프링에서 접근하려면 →
스프링 쪽에서도 동일한 구조를 가진 엔티티 클래스를 정의

interview entity : 시도 횟수 count 가능
interview Result : 완료 횟수 count 가능
status 값은 사용되고 있지 않은 필드임
 */

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "interview") // 장고 테이블과 직접 매핑
public class InterviewSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", referencedColumnName = "id", nullable = false)
    private Account account;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
