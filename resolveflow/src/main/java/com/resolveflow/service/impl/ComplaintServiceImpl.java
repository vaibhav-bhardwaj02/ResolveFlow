package com.resolveflow.service.impl;

import com.resolveflow.dto.complaint.ComplaintHistoryDTO;
import com.resolveflow.dto.complaint.ComplaintRequestDTO;
import com.resolveflow.dto.complaint.ComplaintResponseDTO;
import com.resolveflow.dto.complaint.ComplaintSearchDTO;
import com.resolveflow.dto.complaint.ComplaintStatusDTO;
import com.resolveflow.entity.Category;
import com.resolveflow.entity.Complaint;
import com.resolveflow.entity.User;
import com.resolveflow.enums.ComplaintStatus;
import com.resolveflow.enums.Role;
import com.resolveflow.exception.ResourceNotFoundException;
import com.resolveflow.mapper.ComplaintMapper;
import com.resolveflow.repository.CategoryRepository;
import com.resolveflow.repository.ComplaintRepository;
import com.resolveflow.repository.UserRepository;
import com.resolveflow.service.interfaces.ComplaintService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    private boolean isAuthorized(Complaint complaint, Long requesterId, Role requesterRole) {
        if (requesterRole == Role.ADMIN) {
            return true;
        }
        if (requesterRole == Role.SUPPORT_AGENT) {
            return complaint.getAssignedAgent() != null
                    && complaint.getAssignedAgent().getId().equals(requesterId);
        }
        return complaint.getCustomer() != null
                && complaint.getCustomer().getId().equals(requesterId);
    }

    @Override
    public ComplaintResponseDTO raiseComplaint(ComplaintRequestDTO requestDTO) {

        User customer = userRepository.findById(requestDTO.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found."));

        Category category = categoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found."));

        Complaint complaint = ComplaintMapper.toEntity(requestDTO, customer, category);

        complaint.setStatus(ComplaintStatus.SUBMITTED);
        complaint.setComplaintNumber("CMP-" + System.currentTimeMillis());

        Complaint savedComplaint = complaintRepository.save(complaint);

        return ComplaintMapper.toResponseDTO(savedComplaint);
    }

    @Override
    public ComplaintResponseDTO getComplaintById(Long id, Long requesterId, Role requesterRole) {

        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with id: " + id));

        if (!isAuthorized(complaint, requesterId, requesterRole)) {
            throw new AccessDeniedException("You do not have permission to view this complaint.");
        }

        return ComplaintMapper.toResponseDTO(complaint);
    }

    @Override
    public List<ComplaintResponseDTO> getAllComplaints() {
        return complaintRepository.findAll()
                .stream()
                .map(ComplaintMapper::toResponseDTO)
                .toList();
    }

    @Override
    public List<ComplaintResponseDTO> getMyAssignedComplaints(Long agentId) {
        return complaintRepository.findByAssignedAgentId(agentId)
                .stream()
                .map(ComplaintMapper::toResponseDTO)
                .toList();
    }

    @Override
    public List<ComplaintHistoryDTO> getComplaintHistory(Long customerId) {
        return complaintRepository.findByCustomerIdOrderByCreatedAtDesc(customerId)
                .stream()
                .map(ComplaintMapper::toHistoryDTO)
                .toList();
    }

    @Override
    public List<ComplaintResponseDTO> searchComplaints(ComplaintSearchDTO searchDTO, Long requesterId, Role requesterRole) {

        List<Complaint> complaints;

        if (searchDTO.getComplaintNumber() != null && !searchDTO.getComplaintNumber().isBlank()) {
            complaints = complaintRepository.findByComplaintNumber(searchDTO.getComplaintNumber())
                    .map(List::of)
                    .orElseGet(List::of);
        } else if (searchDTO.getTitle() != null && !searchDTO.getTitle().isBlank()) {
            complaints = complaintRepository.findByTitleContainingIgnoreCase(searchDTO.getTitle());
        } else if (searchDTO.getStatus() != null) {
            complaints = complaintRepository.findByStatus(searchDTO.getStatus());
        } else if (searchDTO.getPriority() != null) {
            complaints = complaintRepository.findByPriority(searchDTO.getPriority());
        } else if (searchDTO.getCategoryId() != null) {
            complaints = complaintRepository.findByCategoryId(searchDTO.getCategoryId());
        } else {
            complaints = complaintRepository.findAll();
        }

        return complaints.stream()
                .filter(c -> isAuthorized(c, requesterId, requesterRole))
                .map(ComplaintMapper::toResponseDTO)
                .toList();
    }

    @Override
    public ComplaintResponseDTO updateComplaint(Long id, ComplaintRequestDTO requestDTO, Long requesterId, Role requesterRole) {

        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with id: " + id));

        if (!isAuthorized(complaint, requesterId, requesterRole)) {
            throw new AccessDeniedException("You do not have permission to edit this complaint.");
        }

        Category category = categoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found."));

        complaint.setTitle(requestDTO.getTitle());
        complaint.setDescription(requestDTO.getDescription());
        complaint.setPriority(requestDTO.getPriority());
        complaint.setCategory(category);
        // Note: complaint.customer is intentionally NOT updated here.
        // Ownership must never change via an edit request - requestDTO.getCustomerId()
        // is accepted (required at creation) but ignored on update.

        Complaint updatedComplaint = complaintRepository.save(complaint);

        return ComplaintMapper.toResponseDTO(updatedComplaint);
    }

    @Override
    public ComplaintResponseDTO updateComplaintStatus(ComplaintStatusDTO statusDTO) {

        Complaint complaint = complaintRepository.findById(statusDTO.getComplaintId())
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with id: " + statusDTO.getComplaintId()));

        complaint.setStatus(statusDTO.getStatus());
        complaint.setResolutionRemarks(statusDTO.getResolutionRemarks());

        Complaint updatedComplaint = complaintRepository.save(complaint);

        return ComplaintMapper.toResponseDTO(updatedComplaint);
    }

    @Override
    public void deleteComplaint(Long id) {

        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with id: " + id));

        complaintRepository.delete(complaint);
    }
}