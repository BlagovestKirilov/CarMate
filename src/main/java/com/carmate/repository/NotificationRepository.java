package com.carmate.repository;

import com.carmate.entity.notification.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query(value = "SELECT * FROM notification e WHERE DATE(e.notification_date) = :date and e.deviceID = :deviceID", nativeQuery = true)
    List<Notification> findByDateAndDeviceID(@Param("date") Date date, @Param("deviceID") String deviceID);

    List<Notification> findAllByDeviceID(String deviceID);
}
