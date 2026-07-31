package com.resolveflow.repository;

import com.resolveflow.entity.Complaint;
import com.resolveflow.entity.User;
import com.resolveflow.enums.ComplaintStatus;
import com.resolveflow.enums.Priority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    Optional<Complaint> findByComplaintNumber(String complaintNumber);

    List<Complaint> findByCustomer(User customer);

    List<Complaint> findByAssignedAgent(User assignedAgent);

    List<Complaint> findByStatus(ComplaintStatus status);

    List<Complaint> findByPriority(Priority priority);
}