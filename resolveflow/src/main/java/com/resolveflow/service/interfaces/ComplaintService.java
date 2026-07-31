package com.resolveflow.service.interfaces;

import com.resolveflow.entity.Complaint;

import java.util.List;
import java.util.Optional;

public interface ComplaintService {

    Complaint save(Complaint complaint);

    Complaint update(Complaint complaint);

    Optional<Complaint> findById(Long id);

    Optional<Complaint> findByComplaintNumber(String complaintNumber);

    List<Complaint> findAll();

    void delete(Long id);
}