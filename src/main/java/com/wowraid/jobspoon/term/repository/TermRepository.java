package com.wowraid.jobspoon.term.repository;

import com.wowraid.jobspoon.term.entity.Term;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TermRepository extends JpaRepository<Term, Long> {
    @EntityGraph(attributePaths = "category") // 목록 조회 시 Lazy 로딩 폭탄 예방을 위해 리포지토리에서 카테고리를 함께 로딩
    Page<Term> findAll(Pageable pageable);

    // Like 기반(대소문자 무시, 태그 포함 옵션) - title/updatedAt 정렬용
    @Query("""
        SELECT DISTINCT t FROM Term t
        LEFT JOIN com.wowraid.jobspoon.term.entity.TermTag tt ON tt.term = t
        LEFT JOIN com.wowraid.jobspoon.term.entity.Tag tag ON tag = tt.tag
        WHERE LOWER(t.title)       LIKE LOWER(CONCAT('%', :q, '%'))
           OR LOWER(t.description) LIKE LOWER(CONCAT('%', :q, '%'))
           OR (:includeTags = TRUE AND LOWER(tag.name) LIKE LOWER(CONCAT('%', :q, '%')))
        """)
    Page<Term> searchLike(@Param("q") String q,
                          @Param("includeTags") boolean includeTags,
                          Pageable pageable);

    // '가장 일치하는 제목' 정렬 — relevance 기본값용(네이티브 + 페이지네이션 + count)
    @Query(value = """
        SELECT t.*,
               (
                 CASE
                   WHEN LOWER(t.title) = LOWER(:q)                      THEN 100
                   WHEN LOWER(t.title) LIKE CONCAT(LOWER(:q), '%')      THEN 80
                   WHEN LOWER(t.title) LIKE CONCAT('%', LOWER(:q), '%') THEN 60
                   ELSE 0
                 END
               )
               + (CASE WHEN LOWER(t.description) LIKE CONCAT('%', LOWER(:q), '%') THEN 10 ELSE 0 END)
               + (CASE WHEN :includeTags = TRUE AND EXISTS (
                       SELECT 1
                         FROM term_tag tt
                         JOIN tag tg ON tg.id = tt.tag_id
                        WHERE tt.term_id = t.id
                          AND LOWER(tg.name) LIKE CONCAT('%', LOWER(:q), '%')
                    ) THEN 5 ELSE 0 END) AS score
        FROM term t
        WHERE
             LOWER(t.title)       LIKE CONCAT('%', LOWER(:q), '%')
          OR LOWER(t.description) LIKE CONCAT('%', LOWER(:q), '%')
          OR (:includeTags = TRUE AND EXISTS (
                SELECT 1 FROM term_tag tt
                JOIN tag tg ON tg.id = tt.tag_id
                WHERE tt.term_id = t.id
                  AND LOWER(tg.name) LIKE CONCAT('%', LOWER(:q), '%')
             ))
        ORDER BY score DESC, t.updated_at DESC, t.title ASC
        """,
            countQuery = """
        SELECT COUNT(*)
          FROM term t
         WHERE
               LOWER(t.title)       LIKE CONCAT('%', LOWER(:q), '%')
            OR LOWER(t.description) LIKE CONCAT('%', LOWER(:q), '%')
            OR (:includeTags = TRUE AND EXISTS (
                  SELECT 1 FROM term_tag tt
                  JOIN tag tg ON tg.id = tt.tag_id
                 WHERE tt.term_id = t.id
                   AND LOWER(tg.name) LIKE CONCAT('%', LOWER(:q), '%')
               ))
        """,
            nativeQuery = true)
    Page<Term> searchByRelevance(@Param("q") String q,
                                 @Param("includeTags") boolean includeTags,
                                 Pageable pageable);
}
