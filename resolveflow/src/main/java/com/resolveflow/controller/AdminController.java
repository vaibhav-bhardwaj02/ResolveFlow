package com.resolveflow.controller;

import com.resolveflow.dto.complaint.ComplaintAssignmentDTO;
import com.resolveflow.dto.user.CreateAgentDTO;
import com.resolveflow.dto.user.UserResponseDTO;
import com.resolveflow.service.interfaces.AdminService;
import com.resolveflow.service.interfaces.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/agents")
    public ResponseEntity<List<UserResponseDTO>> getAllAgents() {
        return ResponseEntity.ok(adminService.getAllAgents());
    }

    @PostMapping("/agents")
    public ResponseEntity<UserResponseDTO> createAgent(@Valid @RequestBody CreateAgentDTO dto) {
        UserResponseDTO response = adminService.createAgent(
                dto.getFirstName(), dto.getLastName(), dto.getEmail(), dto.getPassword(), dto.getPhoneNumber());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/complaints/{id}/assign")
    public ResponseEntity<Void> assignComplaint(@PathVariable Long id, @Valid @RequestBody ComplaintAssignmentDTO dto) {
        adminService.assignComplaintToAgent(id, dto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reports/stats")
    public ResponseEntity<java.util.Map<String, Long>> getStats() {
        return ResponseEntity.ok(adminService.getComplaintStatistics());
    }
}