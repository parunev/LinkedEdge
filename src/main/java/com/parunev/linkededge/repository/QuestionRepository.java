package com.parunev.linkededge.repository;

import com.parunev.linkededge.model.Question;
import com.parunev.linkededge.model.enums.QuestionDifficulty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface QuestionRepository extends JpaRepository<Question, UUID> {

    @Query("SELECT q FROM EDGE_QUESTION q " +
            "WHERE (:skillValue IS NULL OR q.skillValue = :skillValue) " +
            "AND (:difficulty IS NULL OR q.difficulty = :difficulty) " +
            "AND (:experienceId IS NULL OR q.experience.id = :experienceId) " +
            "AND (:educationId IS NULL OR q.education.id = :educationId)")
    Page<Question> findAllQuestions(
            @Param("skillValue")String skill,
            @Param("difficulty")QuestionDifficulty difficulty,
            @Param("experienceId")UUID experienceId,
            @Param("educationId") UUID educationId,
            Pageable pageable);

}
