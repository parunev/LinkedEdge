package com.parunev.linkededge.model;

import com.parunev.linkededge.model.commons.BaseEntity;
import com.parunev.linkededge.model.enums.TokenType;
import jakarta.persistence.*;
import lombok.*;

/**
 * The `JwtToken` class represents a JWT (JSON Web Token) in the LinkedEdge application. It extends the `BaseEntity` class
 * and is typically used for managing user authentication tokens. The `tokenType` field is used to specify the type of the token,
 * which is typically set to BEARER.
 *
 * @author Martin Parunev
 * @date October 11, 2023
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity(name = "JWT_TOKENS")
@AttributeOverride(name = "id", column = @Column(name = "JWT_TOKEN_ID"))
public class JwtToken extends BaseEntity {

    /**
     * The value of the JWT token.
     */
    @Column(name = "TOKEN_VALUE", nullable = false, length = 1000)
    private String tokenValue;

    /**
     * The type of the token, typically set to TOKEN_TYPE_BEARER.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "TOKEN_TYPE", nullable = false)
    private TokenType tokenType;

    /**
     * A flag indicating whether the token has expired.
     */
    @Column(name = "IS_EXPIRED", nullable = false)
    private boolean expired;

    /**
     * A flag indicating whether the token has been revoked.
     */
    @Column(name = "IS_REVOKED", nullable = false)
    private boolean revoked;

    /**
     * The user associated with this JWT token.
     */
    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;
}
