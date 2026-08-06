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
import com.resolveflow.exception.ResourceNotFoundException;
import com.resolveflow.mapper.ComplaintMapper;
import com.resolveflow.repository.CategoryRepository;
import com.resolveflow.repository.ComplaintRepository;
import com.resolveflow.repository.UserRepository;
import com.resolveflow.service.interfaces.ComplaintService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public ComplaintResponseDTO raiseComplaint(ComplaintRequestDTO requestDTO) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<ComplaintResponseDTO> searchComplaints(ComplaintSearchDTO searchDTO) {

        List<Complaint> complaints;

        if (searchDTO.getComplaintNumber() != null && !searchDTO.getComplaintNumber().isBlank()) {

            return complaintRepository.findByComplaintNumber(searchDTO.getComplaintNumber())
                    .map(ComplaintMapper::toResponseDTO)
                    .stream()
                    .toList();

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
                .map(ComplaintMapper::toResponseDTO)
                .toList();
    }

    @Override
    public ComplaintResponseDTO updateComplaint(Long id, ComplaintRequestDTO requestDTO) {

        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Complaint not found with id: " + id));

        User customer = userRepository.findById(requestDTO.getCustomerId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer not found."));

        Category category = categoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found."));

        complaint.setTitle(requestDTO.getTitle());
        complaint.setDescription(requestDTO.getDescription());
        complaint.setPriority(requestDTO.getPriority());
        complaint.setCustomer(customer);
        complaint.setCategory(category);

        Complaint updatedComplaint = complaintRepository.save(complaint);

        return ComplaintMapper.toResponseDTO(updatedComplaint);
    }
    @Override
    public ComplaintResponseDTO updateComplaintStatus(ComplaintStatusDTO statusDTO) {

        Complaint complaint = complaintRepository.findById(statusDTO.getComplaintId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Complaint not found with id: "
                                + statusDTO.getComplaintId()));

        complaint.setStatus(statusDTO.getStatus());
        complaint.setResolutionRemarks(statusDTO.getResolutionRemarks());

        Complaint updatedComplaint = complaintRepository.save(complaint);

        return ComplaintMapper.toResponseDTO(updatedComplaint);
    }

    @Override
    public void deleteComplaint(Long id) {

        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Complaint not found with id: " + id));

        complaintRepository.delete(complaint);
    }
    @Override
    public ComplaintResponseDTO getComplaintById(Long id) {

        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Complaint not found with id: " + id));

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
    public List<ComplaintHistoryDTO> getComplaintHistory(Long customerId) {

        return complaintRepository.findByCustomerIdOrderByCreatedAtDesc(customerId)
                .stream()
                .map(ComplaintMapper::toHistoryDTO)
                .toList();
    }

}