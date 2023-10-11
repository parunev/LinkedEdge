package com.parunev.linkededge.repository;

import com.parunev.linkededge.model.Preparation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PreparationRepository extends JpaRepository<Preparation, UUID> {
}
