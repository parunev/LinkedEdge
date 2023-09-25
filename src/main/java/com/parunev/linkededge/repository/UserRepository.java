package com.parunev.linkededge.repository;

import com.parunev.linkededge.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    @Transactional
    @Modifying
    @Query("UPDATE EDGE_USERS u SET u.isEnabled = TRUE WHERE u.email = ?1")
    void enableAppUser(String email);
}
