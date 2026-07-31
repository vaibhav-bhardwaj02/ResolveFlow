package com.resolveflow.service.impl;

import com.resolveflow.entity.Complaint;
import com.resolveflow.service.interfaces.ComplaintService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ComplaintServiceImpl implements ComplaintService {

    @Override
    public Complaint save(Complaint complaint) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Complaint update(Complaint complaint) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Optional<Complaint> findById(Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Optional<Complaint> findByComplaintNumber(String complaintNumber) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<Complaint> findAll() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void delete(Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}