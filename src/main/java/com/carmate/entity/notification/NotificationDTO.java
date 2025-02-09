package com.carmate.entity.notification;

import lombok.*;

import java.util.Date;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDTO {
    String carName;

    String notificationText;

    String notificationTextEn;

    Date notificationDate;
}
