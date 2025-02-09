package com.carmate.service;

import com.carmate.entity.account.Account;
import com.carmate.entity.car.Car;
import com.carmate.entity.notification.Notification;
import com.carmate.entity.notification.NotificationDTO;
import com.carmate.entity.notification.NotificationType;
import com.carmate.repository.AccountRepository;
import com.carmate.repository.CarRepository;
import com.carmate.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private AccountRepository accountRepository;

    public void generateNotifications(){
        notificationRepository.deleteAll();
        List<Car> cars = carRepository.findAll();
        Date currentDate = new Date();
        List<Notification> resultNotification = new ArrayList<>();
        for(Car car : cars){
            if(car.getIsActiveVignette()) {
                long vignetteExpirationDays = getDaysBetween(currentDate, car.getEndVignetteActiveDate());

                if(vignetteExpirationDays < 300) {
                    Notification vignetteNotification = Notification.builder()
                            .notificationType(NotificationType.VIGNETTE)
                            .notificationText("Винетката на МПС с регистрационен номер " + car.getPlateNumber() + " изтича след " + vignetteExpirationDays +" дни.")
                            .notificationTextEn("Vignette with plate number " + car.getPlateNumber() + " expires in " + vignetteExpirationDays + " days.")
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
                        .notificationTextEn("Vignette with plate number " + car.getPlateNumber() + " is expired.")
                        .notificationDate(currentDate)
                        .account(car.getAccount())
                        .carName(car.getName())
                        .build();

                resultNotification.add(vignetteNotification);
            }

            if(car.getIsActiveInsurance()) {
                long insuranceExpirationDays = getDaysBetween(currentDate, car.getEndInsuranceActiveDate());

                if(insuranceExpirationDays < 300) {
                    Notification insuranceNotification = Notification.builder()
                            .notificationType(NotificationType.INSURANCE)
                            .notificationText("Застраховката на МПС с регистрационен номер " + car.getPlateNumber() + " изтича след " + insuranceExpirationDays +" дни.")
                            .notificationTextEn("Insurance with plate number " + car.getPlateNumber() + " expires in " + insuranceExpirationDays + " days.")
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
                        .notificationTextEn("Insurance with plate number " + car.getPlateNumber() + " is expired.")
                        .notificationDate(currentDate)
                        .account(car.getAccount())
                        .carName(car.getName())
                        .build();

                resultNotification.add(insuranceNotification);
            }

            if(car.getIsActiveTechnicalReview()) {
                long technicalReviewExpirationDays = car.getEndTechnicalReviewActiveDate() != null ?
                        getDaysBetween(currentDate, car.getEndTechnicalReviewActiveDate()) : 365L;

                if(technicalReviewExpirationDays < 300) {
                    Notification technicalReviewNotification = Notification.builder()
                            .notificationType(NotificationType.TECHNICAL_REVIEW)
                            .notificationText("Техническият преглед на МПС с регистрационен номер " + car.getPlateNumber() + " изтича след " + technicalReviewExpirationDays +" дни.")
                            .notificationTextEn("Technical review with plate number " + car.getPlateNumber() + " expires in " + technicalReviewExpirationDays + " days.")
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
                        .notificationTextEn("Technical review with plate number " + car.getPlateNumber() + " is expired.")
                        .notificationDate(currentDate)
                        .deviceID(car.getDeviceID())
                        .carName(car.getName())
                        .build();

                resultNotification.add(technicalReviewNotification);
            }

            if(car.getObligationsCount() > 0){
                Notification obligationNotification = Notification.builder()
                        .notificationType(NotificationType.OBLIGATION)
                        .notificationText("Имате "+ car.getObligationsCount() + " неплатени глоби с МПС с регистрационен номер " + car.getPlateNumber() + " !")
                        .notificationTextEn("You have "+ car.getObligationsCount() + " unpaid fines with vehicle with plate number " + car.getPlateNumber() + " !")
                        .notificationDate(currentDate)
                        .account(car.getAccount())
                        .carName(car.getName())
                        .build();

                resultNotification.add(obligationNotification);
            }
        }
        if(!resultNotification.isEmpty()) {
            notificationRepository.saveAll(resultNotification);
        }
    }


    public List<NotificationDTO> getAllNotificationsByDateAndAccount(){
        String username = getPrincipalUserName();
        Long userId = accountRepository.findByUsername(username)
                .map(Account::getId)
                .orElseThrow(() -> new RuntimeException("User not found"));

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

    public List<Notification> getAllNotificationsByAccount(){
        return notificationRepository.findAllByAccount(accountRepository.findByUsername(getPrincipalUserName()).get());
    }

    private long getDaysBetween(Date dateFrom, Date dateTo){
        return TimeUnit.MILLISECONDS.toDays(dateTo.getTime() - dateFrom.getTime());
    }

    private String getPrincipalUserName() {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
    }
}
