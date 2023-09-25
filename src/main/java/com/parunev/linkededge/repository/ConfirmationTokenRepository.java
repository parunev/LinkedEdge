package com.parunev.linkededge.repository;

import com.parunev.linkededge.model.ConfirmationToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, UUID> {

    List<ConfirmationToken> findAllByUserEmail(String email);

    Optional<ConfirmationToken> findByTokenValue(String token);

    @Transactional
    @Modifying
    @Query("UPDATE CONFIRMATION_TOKENS c SET c.confirmed = ?2 WHERE c.tokenValue = ?1")
    void updateConfirmedAt(String tokenValue, LocalDateTime confirmedAt);
}
