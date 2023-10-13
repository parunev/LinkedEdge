package com.parunev.linkededge.model;

import com.parunev.linkededge.model.commons.BaseEntity;
import com.parunev.linkededge.model.enums.TokenType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
/**
 * The `ConfirmationToken` class represents a confirmation token in the LinkedEdge application. It extends the `BaseEntity` class
 * and is typically used in the process of creating an account or changing the email. The `tokenType` field is used to specify the
 * type of the token, which is typically set to CONFIRMATION.
 *
 * @author Martin Parunev
 * @date October 11, 2023
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity(name = "CONFIRMATION_TOKENS")
@AttributeOverride(name = "id", column = @Column(name = "CONFIRMATION_TOKEN_ID"))
public class ConfirmationToken extends BaseEntity {

    /**
     * The value of the confirmation token.
     */
    @Column(name = "TOKEN_VALUE", nullable = false)
    private String tokenValue;

    /**
     * The type of the token, typically set to CONFIRMATION.
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
     * The user associated with this confirmation token.
     */
    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    /**
     * A method to retrieve the parameters of the token in the format: "TOKEN_TYPE TOKEN_VALUE".
     *
     * @return A string containing the token type and value.
     */
    public String getParameters() {
        return tokenType + " " + tokenValue;
    }
}