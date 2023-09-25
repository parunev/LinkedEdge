package com.parunev.linkededge.model;

import com.parunev.linkededge.model.commons.BaseEntity;
import com.parunev.linkededge.model.enums.TokenType;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity(name = "JWT_TOKENS")
@AttributeOverride(name = "id", column = @Column(name = "JWT_TOKEN_ID"))
public class JwtToken extends BaseEntity {

    @Column(name = "TOKEN_VALUE", nullable = false, length = 1000)
    private String tokenValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "TOKEN_TYPE", nullable = false)
    private TokenType tokenType;

    @Column(name = "IS_EXPIRED", nullable = false)
    private boolean expired;

    @Column(name = "IS_REVOKED", nullable = false)
    private boolean revoked;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;
}
