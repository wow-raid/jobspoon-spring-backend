package com.wowraid.jobspoon.profile_appearance.Repository;

import com.wowraid.jobspoon.profile_appearance.Entity.Title;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TitleRepository extends JpaRepository<Title, Long> {
    List<Title> findAllByAccount_Id(Long accountId);
    Optional<Title> findByIdAndAccount_Id(Long titleId, Long accountId);
    void deleteAllByAccount_Id(Long accountId);
}
