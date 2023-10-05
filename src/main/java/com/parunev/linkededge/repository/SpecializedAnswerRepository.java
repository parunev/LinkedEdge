package com.parunev.linkededge.repository;

import com.parunev.linkededge.model.SpecializedAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SpecializedAnswerRepository extends JpaRepository<SpecializedAnswer, UUID> {
}
