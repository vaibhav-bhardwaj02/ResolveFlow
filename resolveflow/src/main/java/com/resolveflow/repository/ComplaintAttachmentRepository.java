package com.resolveflow.repository;

import com.resolveflow.entity.ComplaintAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComplaintAttachmentRepository
        extends JpaRepository<ComplaintAttachment, Long> {

    // Find all attachments belonging to a complaint
    List<ComplaintAttachment> findByComplaintId(Long complaintId);

    // Find attachments by file name
    List<ComplaintAttachment> findByFileNameContainingIgnoreCase(
            String fileName
    );
}