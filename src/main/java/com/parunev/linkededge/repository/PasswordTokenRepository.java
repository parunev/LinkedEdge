package com.parunev.linkededge.repository;

import com.parunev.linkededge.model.PasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PasswordTokenRepository extends JpaRepository<PasswordToken, UUID> {
    Optional<PasswordToken> findByTokenValue(String token);
}
