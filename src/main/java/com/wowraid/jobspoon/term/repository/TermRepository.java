package com.wowraid.jobspoon.term.repository;

import com.wowraid.jobspoon.term.entity.Term;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TermRepository extends JpaRepository<Term, Long> {

    boolean existsByCategoryIdAndTitle(Long categoryId, String title);
    Optional<Term> findByCategoryIdAndTitle(Long categoryId, String title);

    @EntityGraph(attributePaths = "category")
    Page<Term> findAll(Pageable pageable);

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

    @Query(value = """
        SELECT 
            t.*,
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
        ORDER BY score DESC, t.id DESC, t.title ASC
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

    // 태그 기반 조회 추가
    @Query("""
        select t
        from Term t
        join TermTag tt on tt.term = t
        join tt.tag tg
        where lower(trim(both from tg.name)) = lower(trim(both from :tag))    
    """)
    Page<Term> findByTagNameIgnoreCase(@Param("tag") String tag, org.springframework.data.domain.Pageable pageable);

    @Query(
            value = """
        SELECT *
          FROM term t
         WHERE LEFT(REGEXP_REPLACE(t.title, '^[[:space:][:punct:]]+', ''), 1) >= :fromCh
           AND LEFT(REGEXP_REPLACE(t.title, '^[[:space:][:punct:]]+', ''), 1) <  :toCh
         ORDER BY t.title ASC
        """,
            countQuery = """
        SELECT COUNT(*)
          FROM term t
         WHERE LEFT(REGEXP_REPLACE(t.title, '^[[:space:][:punct:]]+', ''), 1) >= :fromCh
           AND LEFT(REGEXP_REPLACE(t.title, '^[[:space:][:punct:]]+', ''), 1) <  :toCh
        """,
            nativeQuery = true
    )
    Page<Term> findByHangulInitialRange(@Param("fromCh") String fromCh,
                                        @Param("toCh")   String toCh,
                                        Pageable pageable);

    // 알파벳: 첫 글자 대소문자 무시
    @Query(
            value = """
        SELECT *
          FROM term t
         WHERE LOWER(LEFT(t.title, 1)) = LOWER(:ch)
         ORDER BY t.title ASC
        """,
            countQuery = """
        SELECT COUNT(*)
          FROM term t
         WHERE LOWER(LEFT(t.title, 1)) = LOWER(:ch)
        """,
            nativeQuery = true
    )
    Page<Term> findByFirstAlpha(@Param("ch") String ch, Pageable pageable);

    // 기호: 첫 글자 정확히 일치 (LIKE 와일드카드 영향 없음)
    @Query(
            value = """
        SELECT *
          FROM term t
         WHERE LEFT(t.title, 1) = :sym
         ORDER BY t.title ASC
        """,
            countQuery = """
        SELECT COUNT(*)
          FROM term t
         WHERE LEFT(t.title, 1) = :sym
        """,
            nativeQuery = true
    )
    Page<Term> findByFirstSymbol(@Param("sym") String sym, Pageable pageable);
}
