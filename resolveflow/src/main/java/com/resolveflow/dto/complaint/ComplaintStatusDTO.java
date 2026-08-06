package com.resolveflow.dto.complaint;

import com.resolveflow.enums.ComplaintStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintStatusDTO {

    @NotNull(message = "Complaint ID is required")
    private Long complaintId;

    @NotNull(message = "Status is required")
    private ComplaintStatus status;

    @Size(max = 1000, message = "Resolution remarks cannot exceed 1000 characters")
    private String resolutionRemarks;
}