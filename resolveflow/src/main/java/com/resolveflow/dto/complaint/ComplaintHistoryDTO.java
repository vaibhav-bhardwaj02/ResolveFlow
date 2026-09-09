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
public class ComplaintHistoryDTO {

    private Long id;

    private String complaintNumber;

    private String title;

    private Priority priority;

    private ComplaintStatus status;

    private String categoryName;

    private LocalDateTime createdAt;

    private LocalDateTime resolvedAt;
}