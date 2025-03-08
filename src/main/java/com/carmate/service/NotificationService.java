package com.carmate.service;

import com.carmate.entity.account.Account;
import com.carmate.entity.car.Car;
import com.carmate.entity.notification.Notification;
import com.carmate.entity.notification.NotificationDTO;
import com.carmate.entity.notification.NotificationType;
import com.carmate.enums.LanguageEnum;
import com.carmate.repository.AccountRepository;
import com.carmate.repository.CarRepository;
import com.carmate.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    private final CarRepository carRepository;
    private final NotificationRepository notificationRepository;
    private final AccountRepository accountRepository;
    private final AuthService authService;

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    public NotificationService(
            CarRepository carRepository,
            NotificationRepository notificationRepository,
            AccountRepository accountRepository,
            AuthService authService
    ) {
        this.carRepository = carRepository;
        this.notificationRepository = notificationRepository;
        this.accountRepository = accountRepository;
        this.authService = authService;
    }

    @Transactional
    public void generateNotifications() {
        List<Car> cars = carRepository.findAll();
        Date currentDate = new Date();
        List<Notification> resultNotification = new ArrayList<>();
        for (Car car : cars) {
            if (car.getVignette().getIsActive()) {
                long vignetteExpirationDays = getDaysBetween(currentDate, car.getVignette().getEndDate());

                if (vignetteExpirationDays < 300) {
                    Notification vignetteNotification = Notification.builder()
                            .notificationType(NotificationType.VIGNETTE)
                            .notificationText("Винетката на МПС с регистрационен номер " + car.getPlateNumber() + " изтича след " + vignetteExpirationDays + " дни.")
                            .notificationTextEn("Vehicle's vignette with plate number " + car.getPlateNumber() + " expires in " + vignetteExpirationDays + " days.")
                            .notificationDate(currentDate)
                            .account(car.getAccount())
                            .carName(car.getName())
                            .build();

                    resultNotification.add(vignetteNotification);
                }
            } else {
                Notification vignetteNotification = Notification.builder()
                        .notificationType(NotificationType.VIGNETTE)
                        .notificationText("Винетката на МПС с регистрационен номер " + car.getPlateNumber() + " е изтекла!")
                        .notificationTextEn("Vehicle's Vignette with plate number " + car.getPlateNumber() + " is expired.")
                        .notificationDate(currentDate)
                        .account(car.getAccount())
                        .carName(car.getName())
                        .build();

                resultNotification.add(vignetteNotification);
            }

            if (car.getInsurance().getIsActive()) {
                long insuranceExpirationDays = getDaysBetween(currentDate, car.getInsurance().getEndDate());

                if (insuranceExpirationDays < 300) {
                    Notification insuranceNotification = Notification.builder()
                            .notificationType(NotificationType.INSURANCE)
                            .notificationText("Застраховката на МПС с регистрационен номер " + car.getPlateNumber() + " изтича след " + insuranceExpirationDays + " дни.")
                            .notificationTextEn("Vehicle's insurance with plate number " + car.getPlateNumber() + " expires in " + insuranceExpirationDays + " days.")
                            .notificationDate(currentDate)
                            .account(car.getAccount())
                            .carName(car.getName())
                            .build();

                    resultNotification.add(insuranceNotification);
                }
            } else {
                Notification insuranceNotification = Notification.builder()
                        .notificationType(NotificationType.INSURANCE)
                        .notificationText("Застраховката на МПС с регистрационен номер " + car.getPlateNumber() + " е изтекла!")
                        .notificationTextEn("Vehicle's insurance with plate number " + car.getPlateNumber() + " is expired.")
                        .notificationDate(currentDate)
                        .account(car.getAccount())
                        .carName(car.getName())
                        .build();

                resultNotification.add(insuranceNotification);
            }

            if (car.getTechnicalReview().getIsActive()) {
                long technicalReviewExpirationDays = car.getTechnicalReview().getEndDate() != null ?
                        getDaysBetween(currentDate, car.getTechnicalReview().getEndDate()) : 365L;

                if (technicalReviewExpirationDays < 300) {
                    Notification technicalReviewNotification = Notification.builder()
                            .notificationType(NotificationType.TECHNICAL_REVIEW)
                            .notificationText("Техническият преглед на МПС с регистрационен номер " + car.getPlateNumber() + " изтича след " + technicalReviewExpirationDays + " дни.")
                            .notificationTextEn("Vehicle's technical review with plate number " + car.getPlateNumber() + " expires in " + technicalReviewExpirationDays + " days.")
                            .notificationDate(currentDate)
                            .account(car.getAccount())
                            .carName(car.getName())
                            .build();

                    resultNotification.add(technicalReviewNotification);
                }
            } else {
                Notification technicalReviewNotification = Notification.builder()
                        .notificationType(NotificationType.TECHNICAL_REVIEW)
                        .notificationText("Техническият преглед на МПС с регистрационен номер " + car.getPlateNumber() + " е изтекла!")
                        .notificationTextEn("Vehicle's technical review with plate number " + car.getPlateNumber() + " is expired.")
                        .notificationDate(currentDate)
                        .carName(car.getName())
                        .build();

                resultNotification.add(technicalReviewNotification);
            }

            if (car.getObligation().getObligationsCount() > 0) {
                Notification obligationNotification = Notification.builder()
                        .notificationType(NotificationType.OBLIGATION)
                        .notificationText("Имате " + car.getObligation().getObligationsCount() + " неплатени глоби с МПС с регистрационен номер " + car.getPlateNumber() + " !")
                        .notificationTextEn("You have " + car.getObligation().getObligationsCount() + " unpaid fines with vehicle with plate number " + car.getPlateNumber() + " !")
                        .notificationDate(currentDate)
                        .account(car.getAccount())
                        .carName(car.getName())
                        .build();

                resultNotification.add(obligationNotification);
            }
        }
        if (!resultNotification.isEmpty()) {
            notificationRepository.saveAll(resultNotification);
        }
    }


    public List<NotificationDTO> getAllNotificationsByDateAndAccount() {
        Long userId = authService.getAccountByPrincipal().getId();

        List<Notification> notifications = notificationRepository.findByDateAndAccount(new Date(), userId);

        return notifications.stream()
                .map(notification -> new NotificationDTO(
                        notification.getCarName(),
                        notification.getNotificationText(),
                        notification.getNotificationTextEn(),
                        notification.getNotificationDate())
                )
                .collect(Collectors.toList());
    }

    public void saveNotificationToken(String notificationToken) {
        Account account = authService.getAccountByPrincipal();
        account.setNotificationToken(notificationToken);
        accountRepository.save(account);
    }

    public void sendCurrentDateNotification() {
        List<Notification> currentDayNotifications = notificationRepository.findAllCurrentDayNotifications();
        for (Notification notification : currentDayNotifications) {
            try {
                sendNotification("CAR MATE",
                        notification.getAccount().getLanguage().equals(LanguageEnum.BULGARIAN) ?
                                notification.getNotificationText() : notification.getNotificationTextEn(),
                        notification.getAccount().getNotificationToken());
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }


    public void sendNotification(String title, String body, String notificationToken) {
        String expoPushUrl = "https://exp.host/--/api/v2/push/send";

        String requestBody = "{"
                + "\"to\": \"" + notificationToken + "\","
                + "\"title\": \"" + title + "\","
                + "\"body\": \"" + body + "\""
                + "}";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.exchange(expoPushUrl, HttpMethod.POST, entity, String.class);
    }

    public void changeAccountLanguage(String language) {
        Account account = authService.getAccountByPrincipal();
        if (language.equals("en")) {
            account.setLanguage(LanguageEnum.ENGLISH);
        } else {
            account.setLanguage(LanguageEnum.BULGARIAN);
        }
        accountRepository.save(account);
    }

    private long getDaysBetween(Date dateFrom, Date dateTo) {
        return TimeUnit.MILLISECONDS.toDays(dateTo.getTime() - dateFrom.getTime());
    }
}
