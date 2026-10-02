package com.resolveflow.service.impl;

import com.resolveflow.dto.complaint.ComplaintAssignmentDTO;
import com.resolveflow.dto.user.UserResponseDTO;
import com.resolveflow.entity.Complaint;
import com.resolveflow.entity.User;
import com.resolveflow.enums.ComplaintStatus;
import com.resolveflow.enums.Role;
import com.resolveflow.exception.ResourceNotFoundException;
import com.resolveflow.mapper.UserMapper;
import com.resolveflow.repository.ComplaintRepository;
import com.resolveflow.repository.UserRepository;
import com.resolveflow.service.interfaces.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.resolveflow.enums.NotificationType;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final ComplaintRepository complaintRepository;
    private final PasswordEncoder passwordEncoder;
    private final com.resolveflow.service.interfaces.NotificationService notificationService;

    @Override
    public List<UserResponseDTO> getAllAgents() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.SUPPORT_AGENT)
                .map(UserMapper::toResponseDTO)
                .toList();
    }

    @Override
    public UserResponseDTO createAgent(String firstName, String lastName, String email, String password, String phoneNumber) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email is already registered.");
        }

        User agent = new User();
        agent.setFirstName(firstName);
        agent.setLastName(lastName);
        agent.setEmail(email);
        agent.setPassword(passwordEncoder.encode(password));
        agent.setPhoneNumber(phoneNumber);
        agent.setRole(Role.SUPPORT_AGENT);

        return UserMapper.toResponseDTO(userRepository.save(agent));
    }

    @Override
    public void assignComplaintToAgent(Long complaintId, ComplaintAssignmentDTO assignmentDTO) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with id: " + complaintId));

        User agent = userRepository.findById(assignmentDTO.getAgentId())
                .orElseThrow(() -> new ResourceNotFoundException("Agent not found with id: " + assignmentDTO.getAgentId()));

        if (agent.getRole() != Role.SUPPORT_AGENT) {
            throw new IllegalArgumentException("Selected user is not a support agent.");
        }

        complaint.setAssignedAgent(agent);
        complaint.setStatus(ComplaintStatus.ASSIGNED);
        notificationService.create(agent, NotificationType.COMPLAINT_ASSIGNED,
                "New Complaint Assigned", "Complaint #" + complaint.getComplaintNumber() + " has been assigned to you.");
        complaintRepository.save(complaint);
    }

    @Override
    public java.util.Map<String, Long> getComplaintStatistics() {
        java.util.Map<String, Long> stats = new java.util.LinkedHashMap<>();
        for (ComplaintStatus status : ComplaintStatus.values()) {
            stats.put(status.name(), complaintRepository.countByStatus(status));
        }
        return stats;
    }
}