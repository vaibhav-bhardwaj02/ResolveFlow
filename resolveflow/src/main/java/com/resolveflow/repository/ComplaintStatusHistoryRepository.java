package com.resolveflow.repository;

import com.resolveflow.entity.ComplaintStatusHistory;
import com.resolveflow.entity.User;
import com.resolveflow.enums.ComplaintStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComplaintStatusHistoryRepository
        extends JpaRepository<ComplaintStatusHistory, Long> {

    // Find complete status history of a complaint
    List<ComplaintStatusHistory> findByComplaintIdOrderByCreatedAtDesc(
            Long complaintId
    );

    // Find status history by status
    List<ComplaintStatusHistory> findByStatus(
            ComplaintStatus status
    );

    // Find status updates made by a specific user
    List<ComplaintStatusHistory> findByUpdatedBy(
            User updatedBy
    );
}