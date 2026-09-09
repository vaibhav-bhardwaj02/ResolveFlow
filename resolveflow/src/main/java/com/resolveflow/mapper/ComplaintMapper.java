package com.resolveflow.mapper;

import com.resolveflow.dto.complaint.ComplaintHistoryDTO;
import com.resolveflow.dto.complaint.ComplaintResponseDTO;
import com.resolveflow.dto.complaint.ComplaintRequestDTO;
import com.resolveflow.entity.Category;
import com.resolveflow.entity.Complaint;
import com.resolveflow.entity.User;

public class ComplaintMapper {

    private ComplaintMapper() {
        // Prevent instantiation
    }

    /**
     * Convert Complaint Entity to ComplaintResponseDTO
     */
    public static ComplaintResponseDTO toResponseDTO(Complaint complaint) {

        if (complaint == null) {
            return null;
        }

        return ComplaintResponseDTO.builder()
                .id(complaint.getId())
                .complaintNumber(complaint.getComplaintNumber())
                .title(complaint.getTitle())
                .description(complaint.getDescription())
                .priority(complaint.getPriority())
                .status(complaint.getStatus())
                .resolutionRemarks(complaint.getResolutionRemarks())
                .resolvedAt(complaint.getResolvedAt())
                .createdAt(complaint.getCreatedAt())
                .updatedAt(complaint.getUpdatedAt())

                .categoryId(getCategoryId(complaint.getCategory()))
                .categoryName(getCategoryName(complaint.getCategory()))

                .customerId(getUserId(complaint.getCustomer()))
                .customerName(getUserFullName(complaint.getCustomer()))

                .assignedAgentId(getUserId(complaint.getAssignedAgent()))
                .assignedAgentName(getUserFullName(complaint.getAssignedAgent()))

                .build();
    }

    /**
     * Convert Complaint Entity to ComplaintHistoryDTO
     */
    public static ComplaintHistoryDTO toHistoryDTO(Complaint complaint) {

        if (complaint == null) {
            return null;
        }

        return ComplaintHistoryDTO.builder()
                .id(complaint.getId())
                .complaintNumber(complaint.getComplaintNumber())
                .title(complaint.getTitle())
                .priority(complaint.getPriority())
                .status(complaint.getStatus())
                .categoryName(getCategoryName(complaint.getCategory()))
                .createdAt(complaint.getCreatedAt())
                .resolvedAt(complaint.getResolvedAt())
                .build();
    }

    private static Long getCategoryId(Category category) {
        return category != null ? category.getId() : null;
    }

    private static String getCategoryName(Category category) {
        return category != null ? category.getName() : null;
    }

    private static Long getUserId(User user) {
        return user != null ? user.getId() : null;
    }

    private static String getUserFullName(User user) {

        if (user == null) {
            return null;
        }

        return user.getFirstName() + " " + user.getLastName();
    }

    public static Complaint toEntity(
            ComplaintRequestDTO dto,
            User customer,
            Category category
    ) {

        if (dto == null) {
            return null;
        }

        Complaint complaint = new Complaint();

        complaint.setTitle(dto.getTitle());
        complaint.setDescription(dto.getDescription());
        complaint.setPriority(dto.getPriority());

        complaint.setCustomer(customer);
        complaint.setCategory(category);

        return complaint;
    }

}