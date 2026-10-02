package com.resolveflow.service.impl;

import com.resolveflow.dto.notification.NotificationDTO;
import com.resolveflow.entity.Notification;
import com.resolveflow.entity.User;
import com.resolveflow.enums.NotificationType;
import com.resolveflow.mapper.NotificationMapper;
import com.resolveflow.repository.NotificationRepository;
import com.resolveflow.service.interfaces.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }

    @Override
    public void create(User user, NotificationType type, String subject, String message) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(type);
        notification.setSubject(subject);
        notification.setMessage(message);
        notification.setSent(true);
        notification.setSentAt(LocalDateTime.now());
        notificationRepository.save(notification);
        System.out.println("[NOTIFICATION] To " + user.getEmail() + ": " + subject + " - " + message);
    }

    @Override
    public List<NotificationDTO> getMyNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(NotificationMapper::toDTO).toList();
    }
}