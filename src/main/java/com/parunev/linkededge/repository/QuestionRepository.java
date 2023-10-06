package com.parunev.linkededge.repository;

import com.parunev.linkededge.model.Question;
import com.parunev.linkededge.model.SpecializedAnswer;
import com.parunev.linkededge.model.enums.QuestionDifficulty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface QuestionRepository extends JpaRepository<Question, UUID> {

    @Query("SELECT q FROM EDGE_QUESTION q " +
            "WHERE (:skillValue IS NULL OR q.skillValue = :skillValue) " +
            "AND (:difficulty IS NULL OR q.difficulty = :difficulty) " +
            "AND (:experienceId IS NULL OR q.experience.id = :experienceId) " +
            "AND (:educationId IS NULL OR q.education.id = :educationId) " +
            "AND (:profileId IS NULL OR q.profile.id = :profileId)")
    Page<Question> findAllQuestions(
            @Param("skillValue")String skill,
            @Param("difficulty")QuestionDifficulty difficulty,
            @Param("experienceId")UUID experienceId,
            @Param("educationId") UUID educationId,
            @Param("profileId") UUID profileId,
            Pageable pageable);

    @Query("SELECT sa FROM EDGE_SPECIALIZED_ANSWER sa WHERE " +
            "(:fromDate IS NULL OR sa.creationTimestamp BETWEEN :fromDate AND :toDate) " +
            "AND (:input IS NULL OR " +
            "   sa.answer LIKE %:input% OR " +
            "   sa.benefits LIKE %:input% OR " +
            "   sa.example LIKE %:input% OR " +
            "   sa.question LIKE %:input%) " +
            "AND (:profileId IS NULL OR sa.profile.id = :profileId)")
    Page<SpecializedAnswer> findAllAnswers(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("input") String input,
            @Param("profileId") UUID profileId,
            Pageable pageable);
}
