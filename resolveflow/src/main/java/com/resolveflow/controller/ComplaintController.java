package com.resolveflow.controller;

import com.resolveflow.dto.complaint.ComplaintHistoryDTO;
import com.resolveflow.dto.complaint.ComplaintRequestDTO;
import com.resolveflow.dto.complaint.ComplaintResponseDTO;
import com.resolveflow.dto.complaint.ComplaintSearchDTO;
import com.resolveflow.dto.complaint.ComplaintStatusDTO;
import com.resolveflow.service.interfaces.ComplaintService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/complaints")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService complaintService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ComplaintResponseDTO raiseComplaint(
            @Valid @RequestBody ComplaintRequestDTO requestDTO) {

        return complaintService.raiseComplaint(requestDTO);
    }

    @GetMapping("/{id}")
    public ComplaintResponseDTO getComplaintById(@PathVariable Long id) {

        return complaintService.getComplaintById(id);
    }

    @GetMapping
    public List<ComplaintResponseDTO> getAllComplaints() {

        return complaintService.getAllComplaints();
    }

    @GetMapping("/history/{customerId}")
    public List<ComplaintHistoryDTO> getComplaintHistory(
            @PathVariable Long customerId) {

        return complaintService.getComplaintHistory(customerId);
    }

    @PostMapping("/search")
    public List<ComplaintResponseDTO> searchComplaints(
            @RequestBody ComplaintSearchDTO searchDTO) {

        return complaintService.searchComplaints(searchDTO);
    }

    @PutMapping("/{id}")
    public ComplaintResponseDTO updateComplaint(
            @PathVariable Long id,
            @Valid @RequestBody ComplaintRequestDTO requestDTO) {

        return complaintService.updateComplaint(id, requestDTO);
    }

    @PatchMapping("/status")
    public ComplaintResponseDTO updateComplaintStatus(
            @Valid @RequestBody ComplaintStatusDTO statusDTO) {

        return complaintService.updateComplaintStatus(statusDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComplaint(@PathVariable Long id) {

        complaintService.deleteComplaint(id);
    }
}