package com.parunev.linkededge.repository;

import com.parunev.linkededge.model.Coaching;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CoachingRepository extends JpaRepository<Coaching, UUID> {
}
