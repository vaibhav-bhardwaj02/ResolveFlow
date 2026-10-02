package com.resolveflow.mapper;

import com.resolveflow.dto.notification.NotificationDTO;
import com.resolveflow.entity.Notification;

public class NotificationMapper {

    private NotificationMapper() {}

    public static NotificationDTO toDTO(Notification n) {
        if (n == null) return null;
        return NotificationDTO.builder()
                .id(n.getId())
                .type(n.getType())
                .subject(n.getSubject())
                .message(n.getMessage())
                .sent(n.getSent())
                .sentAt(n.getSentAt())
                .createdAt(n.getCreatedAt())
                .build();
    }
}