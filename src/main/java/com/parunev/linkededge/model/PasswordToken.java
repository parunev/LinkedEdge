package com.parunev.linkededge.model;

import com.parunev.linkededge.model.commons.BaseEntity;
import com.parunev.linkededge.model.enums.TokenType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity(name = "PASSWORD_RESET_TOKENS")
@AttributeOverride(name = "id", column = @Column(name = "PASSWORD_RESET_TOKEN_ID"))
public class PasswordToken extends BaseEntity {

    @Column(name = "TOKEN_VALUE", nullable = false)
    private String tokenValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "TOKEN_TYPE", nullable = false)
    private TokenType tokenType;

    @Column(name = "CONFIRMED")
    private LocalDateTime confirmed;

    @Column(name = "EXPIRES", nullable = false)
    private LocalDateTime expires;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;
}
