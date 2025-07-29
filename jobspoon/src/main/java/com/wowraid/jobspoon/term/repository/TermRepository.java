package com.wowraid.jobspoon.term.repository;

import com.wowraid.jobspoon.term.entity.Term;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TermRepository extends JpaRepository<Term, Long> {
    Page<Term> findAll(Pageable pageable);
}
