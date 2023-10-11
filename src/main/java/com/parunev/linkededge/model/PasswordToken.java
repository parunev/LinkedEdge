package com.parunev.linkededge.model;

import com.parunev.linkededge.model.commons.BaseEntity;
import com.parunev.linkededge.model.enums.TokenType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * The `PasswordToken` class represents a password reset token in the LinkedEdge application. It extends the `BaseEntity` class
 * and is typically used when a user requests to change or reset their password. The `tokenType` field is used to specify the
 * type of the token, which is typically set to PASSWORD.
 *
 * @author Martin Parunev
 * @date October 11, 2023
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity(name = "PASSWORD_RESET_TOKENS")
@AttributeOverride(name = "id", column = @Column(name = "PASSWORD_RESET_TOKEN_ID"))
public class PasswordToken extends BaseEntity {

    /**
     * The value of the password reset token.
     */
    @Column(name = "TOKEN_VALUE", nullable = false)
    private String tokenValue;

    /**
     * The type of the token, typically set to PASSWORD.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "TOKEN_TYPE", nullable = false)
    private TokenType tokenType;

    /**
     * The timestamp when the token is confirmed.
     */
    @Column(name = "CONFIRMED")
    private LocalDateTime confirmed;

    /**
     * The timestamp when the token expires.
     */
    @Column(name = "EXPIRES", nullable = false)
    private LocalDateTime expires;

    /**
     * The user associated with this password reset token.
     */
    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;
}
