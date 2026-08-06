package com.resolveflow.dto.complaint;

import com.resolveflow.enums.ComplaintStatus;
import com.resolveflow.enums.Priority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintResponseDTO {

    private Long id;

    private String complaintNumber;

    private String title;

    private String description;

    private Priority priority;

    private ComplaintStatus status;

    private String categoryName;

    private Long categoryId;

    private Long customerId;

    private String customerName;

    private Long assignedAgentId;

    private String assignedAgentName;

    private String resolutionRemarks;

    private LocalDateTime resolvedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}