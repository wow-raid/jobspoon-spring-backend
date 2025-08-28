package com.wowraid.jobspoon.term.repository;

import com.wowraid.jobspoon.term.entity.Term;
import com.wowraid.jobspoon.term.entity.TermTag;
import com.wowraid.jobspoon.term.entity.TermTagId;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TermTagRepository extends JpaRepository<TermTag, TermTagId> {
    @Modifying
    @Transactional
    @Query("DELETE FROM TermTag tt WHERE tt.term = :term")
    void deleteByTerm(@Param("term") Term term);

    interface Row {
        Long getTermId();
        String getTagName();
    }

    @Query("""
       select tt.term.id as termId, tg.name as tagName
       from TermTag tt
       join tt.tag tg
       where tt.term.id in :ids
    """)
    List<Row> findTermIdAndTagNameByTermIdIn(@Param("ids") List<Long> ids);
}
