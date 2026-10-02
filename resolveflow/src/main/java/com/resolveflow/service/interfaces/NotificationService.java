package com.resolveflow.service.interfaces;

import com.resolveflow.dto.notification.NotificationDTO;
import com.resolveflow.entity.Notification;
import com.resolveflow.entity.User;
import com.resolveflow.enums.NotificationType;

import java.util.List;

public interface NotificationService {
    Notification save(Notification notification);
    void create(User user, NotificationType type, String subject, String message);
    List<NotificationDTO> getMyNotifications(Long userId);
}