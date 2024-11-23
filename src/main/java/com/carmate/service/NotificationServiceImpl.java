package com.carmate.service;

import com.carmate.entity.car.Car;
import com.carmate.entity.notification.Notification;
import com.carmate.entity.notification.NotificationType;
import com.carmate.repository.CarRepository;
import com.carmate.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class NotificationServiceImpl {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private NotificationRepository notificationRepository;

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
                            .deviceID(car.getDeviceID())
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
                        .deviceID(car.getDeviceID())
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
                            .deviceID(car.getDeviceID())
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
                        .deviceID(car.getDeviceID())
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
                            .deviceID(car.getDeviceID())
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
                        .deviceID(car.getDeviceID())
                        .carName(car.getName())
                        .build();

                resultNotification.add(obligationNotification);
            }
        }
        if(!resultNotification.isEmpty()) {
            notificationRepository.saveAll(resultNotification);
        }
    }


    public List<Notification> getAllNotificationsByDateAndDeviceId(String deviceID){
        Date currentDate = new Date();
        return notificationRepository.findByDateAndDeviceID(currentDate, deviceID);
    }

    public List<Notification> getAllNotificationsBydDeviceId(String deviceID){
        return notificationRepository.findAllByDeviceID( deviceID);
    }
    private long getDaysBetween(Date dateFrom, Date dateTo){
        return TimeUnit.MILLISECONDS.toDays(dateTo.getTime() - dateFrom.getTime());
    }
}
