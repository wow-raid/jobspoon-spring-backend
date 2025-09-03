package com.wowraid.jobspoon.term.repository;

import com.wowraid.jobspoon.term.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findById(Long categoryId);
    Optional<Category> findFirstByNameAndDepth(String name, Integer depth);
}
