package com.parunev.linkededge.repository;

import com.parunev.linkededge.model.JwtToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JwtTokenRepository extends JpaRepository<JwtToken, UUID> {
    Optional<JwtToken> findByTokenValue(String token);

    @Query("""
        SELECT J FROM JWT_TOKENS J INNER JOIN EDGE_USERS U ON J.user.id = U.id
        WHERE U.id = :id AND (J.expired = false OR J.revoked = false)
    """)
    List<JwtToken> findAllValidTokenByUserId(UUID id);
}
