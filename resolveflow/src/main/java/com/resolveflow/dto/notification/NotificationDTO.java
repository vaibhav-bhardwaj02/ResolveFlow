package com.resolveflow.dto.notification;

import com.resolveflow.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long id;
    private NotificationType type;
    private String subject;
    private String message;
    private Boolean sent;
    private LocalDateTime sentAt;
    private LocalDateTime createdAt;
}