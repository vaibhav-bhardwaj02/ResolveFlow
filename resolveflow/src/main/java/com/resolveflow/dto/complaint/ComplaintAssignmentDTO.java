package com.resolveflow.dto.complaint;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintAssignmentDTO {

    @NotNull(message = "Agent ID is required")
    private Long agentId;
}