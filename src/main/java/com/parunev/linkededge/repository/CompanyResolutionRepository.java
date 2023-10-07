package com.parunev.linkededge.repository;

import com.parunev.linkededge.model.job.CompanyResolution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CompanyResolutionRepository extends JpaRepository<CompanyResolution, UUID> {
}
