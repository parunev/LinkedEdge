package com.parunev.linkededge.repository;

import com.parunev.linkededge.model.InterviewPreparation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface InterviewPreparationRepository extends JpaRepository<InterviewPreparation, UUID> {
}
