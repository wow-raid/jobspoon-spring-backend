package com.wowraid.jobspoon.term.repository;

import com.wowraid.jobspoon.term.entity.TermTag;
import com.wowraid.jobspoon.term.entity.TermTagId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TermTagRepository extends JpaRepository<TermTag, TermTagId> {
}
