package com.resolveflow.service.interfaces;

import com.resolveflow.dto.complaint.ComplaintAssignmentDTO;
import com.resolveflow.dto.user.UserResponseDTO;

import java.util.List;

public interface AdminService {
    List<UserResponseDTO> getAllAgents();
    UserResponseDTO createAgent(String firstName, String lastName, String email, String password, String phoneNumber);
    void assignComplaintToAgent(Long complaintId, ComplaintAssignmentDTO assignmentDTO);

    java.util.Map<String, Long> getComplaintStatistics();
}