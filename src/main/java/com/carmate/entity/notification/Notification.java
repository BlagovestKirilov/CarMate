package com.carmate.entity.notification;

import com.carmate.entity.account.Account;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    private String notificationText;

    private String notificationTextEn;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    private Date notificationDate;

    private String vehicleName;
}
