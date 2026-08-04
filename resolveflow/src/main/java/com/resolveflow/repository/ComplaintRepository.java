package com.resolveflow.repository;

import com.resolveflow.entity.Complaint;
import com.resolveflow.entity.User;
import com.resolveflow.enums.ComplaintStatus;
import com.resolveflow.enums.Priority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    // Find a complaint by its unique complaint number
    Optional<Complaint> findByComplaintNumber(String complaintNumber);

    // Find all complaints raised by a customer
    List<Complaint> findByCustomer(User customer);

    // Find all complaints assigned to an agent
    List<Complaint> findByAssignedAgent(User assignedAgent);

    // Find complaints by status
    List<Complaint> findByStatus(ComplaintStatus status);

    // Find complaints by priority
    List<Complaint> findByPriority(Priority priority);

    // Search complaints by title
    List<Complaint> findByTitleContainingIgnoreCase(String title);

    // Search complaints by title or description
    List<Complaint> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String title,
            String description
    );

    // Find complaints by category
    List<Complaint> findByCategoryId(Long categoryId);
}