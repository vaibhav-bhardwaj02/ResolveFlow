package com.resolveflow.dto.complaint;

import com.resolveflow.enums.ComplaintStatus;
import com.resolveflow.enums.Priority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintSearchDTO {

    private String complaintNumber;

    private String title;

    private ComplaintStatus status;

    private Priority priority;

    private Long categoryId;

    private Long customerId;

    private Long assignedAgentId;
}