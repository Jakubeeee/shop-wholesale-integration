package com.jakubeeee.security.entity;

import com.jakubeeee.common.persistence.IdentifiableEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Immutable;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Immutable entity representing an unique token serving as user validator in the password reset functionality.
 */
@Getter
@EqualsAndHashCode(callSuper = false)
@ToString
@NoArgsConstructor
@Immutable
@Entity
@Table(name = "PASSWORD_RESET_TOKENS")
public class PasswordResetToken extends IdentifiableEntity {

    @Column(name = "VALUE", unique = true, nullable = false, updatable = false)
    private String value;

    @Column(name = "EXPIRY_DATE", nullable = false, updatable = false)
    private LocalDateTime expiryDate;

    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false, updatable = false)
    private User user;

    public PasswordResetToken(String value, User user, LocalDateTime creationDateTime, int expirationInMinutes) {
        this.value = value;
        this.user = user;
        this.expiryDate = creationDateTime.plusMinutes(expirationInMinutes);
    }

}
