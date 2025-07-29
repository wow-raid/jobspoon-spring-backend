package com.wowraid.jobspoon.term.repository;

import com.wowraid.jobspoon.term.entity.Term;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TermRepository extends JpaRepository<Term, Long> {
}
