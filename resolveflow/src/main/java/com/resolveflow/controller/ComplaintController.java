package com.resolveflow.controller;

import com.resolveflow.dto.complaint.ComplaintHistoryDTO;
import com.resolveflow.dto.complaint.ComplaintRequestDTO;
import com.resolveflow.dto.complaint.ComplaintResponseDTO;
import com.resolveflow.dto.complaint.ComplaintSearchDTO;
import com.resolveflow.dto.complaint.ComplaintStatusDTO;
import com.resolveflow.security.UserPrincipal;
import com.resolveflow.service.interfaces.ComplaintService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/complaints")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService complaintService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ComplaintResponseDTO raiseComplaint(@Valid @RequestBody ComplaintRequestDTO requestDTO) {
        return complaintService.raiseComplaint(requestDTO);
    }

    @GetMapping("/{id}")
    public ComplaintResponseDTO getComplaintById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        return complaintService.getComplaintById(id, principal.getId(), principal.getUser().getRole());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPORT_AGENT')")
    public List<ComplaintResponseDTO> getAllComplaints() {
        return complaintService.getAllComplaints();
    }

    @GetMapping("/my")
    public List<ComplaintHistoryDTO> getMyComplaintHistory(@AuthenticationPrincipal UserPrincipal principal) {
        return complaintService.getComplaintHistory(principal.getId());
    }

    @GetMapping("/assigned/me")
    @PreAuthorize("hasRole('SUPPORT_AGENT')")
    public List<ComplaintResponseDTO> getMyAssignedComplaints(@AuthenticationPrincipal UserPrincipal principal) {
        return complaintService.getMyAssignedComplaints(principal.getId());
    }

    @GetMapping("/history/{customerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPORT_AGENT')")
    public List<ComplaintHistoryDTO> getComplaintHistory(@PathVariable Long customerId) {
        return complaintService.getComplaintHistory(customerId);
    }

    @PostMapping("/search")
    public List<ComplaintResponseDTO> searchComplaints(
            @RequestBody ComplaintSearchDTO searchDTO,
            @AuthenticationPrincipal UserPrincipal principal) {
        return complaintService.searchComplaints(searchDTO, principal.getId(), principal.getUser().getRole());
    }

    @PutMapping("/{id}")
    public ComplaintResponseDTO updateComplaint(
            @PathVariable Long id,
            @Valid @RequestBody ComplaintRequestDTO requestDTO,
            @AuthenticationPrincipal UserPrincipal principal) {
        return complaintService.updateComplaint(id, requestDTO, principal.getId(), principal.getUser().getRole());
    }

    @PatchMapping("/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPORT_AGENT')")
    public ComplaintResponseDTO updateComplaintStatus(@Valid @RequestBody ComplaintStatusDTO statusDTO) {
        return complaintService.updateComplaintStatus(statusDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteComplaint(@PathVariable Long id) {
        complaintService.deleteComplaint(id);
    }
}