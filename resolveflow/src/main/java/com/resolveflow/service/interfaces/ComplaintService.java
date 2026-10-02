package com.resolveflow.service.interfaces;

import com.resolveflow.dto.complaint.ComplaintHistoryDTO;
import com.resolveflow.dto.complaint.ComplaintRequestDTO;
import com.resolveflow.dto.complaint.ComplaintResponseDTO;
import com.resolveflow.dto.complaint.ComplaintSearchDTO;
import com.resolveflow.dto.complaint.ComplaintStatusDTO;
import com.resolveflow.enums.Role;

import java.util.List;

public interface ComplaintService {

    ComplaintResponseDTO raiseComplaint(ComplaintRequestDTO requestDTO);

    ComplaintResponseDTO getComplaintById(Long id, Long requesterId, Role requesterRole);

    List<ComplaintResponseDTO> getAllComplaints();

    List<ComplaintResponseDTO> getMyAssignedComplaints(Long agentId);

    List<ComplaintHistoryDTO> getComplaintHistory(Long customerId);

    List<ComplaintResponseDTO> searchComplaints(ComplaintSearchDTO searchDTO, Long requesterId, Role requesterRole);

    ComplaintResponseDTO updateComplaint(Long id, ComplaintRequestDTO requestDTO, Long requesterId, Role requesterRole);

    ComplaintResponseDTO updateComplaintStatus(ComplaintStatusDTO statusDTO);

    void deleteComplaint(Long id);
}