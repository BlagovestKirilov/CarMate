package com.carmate.entity.account;

import com.carmate.enums.AccountRoleEnum;
import com.carmate.enums.LanguageEnum;
import com.carmate.enums.RegistrationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountRegistrationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String accountName;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String confirmationCode;

    @Enumerated(EnumType.STRING)
    private AccountRoleEnum role;

    @Enumerated(EnumType.STRING)
    private LanguageEnum language;

    private Date date = new Date();

    @Enumerated(EnumType.STRING)
    private RegistrationStatus status = RegistrationStatus.PENDING;
}
