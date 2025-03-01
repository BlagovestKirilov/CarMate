package com.carmate.entity.account;

import com.carmate.enums.ForgotPasswordStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountForgotPasswordRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    private String token;

    @Column(nullable = false)
    private String confirmationCode;

    private Date date = new Date();

    @Enumerated(EnumType.STRING)
    private ForgotPasswordStatus status = ForgotPasswordStatus.PENDING;
}
