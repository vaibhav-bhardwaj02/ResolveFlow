package com.resolveflow.service.interfaces;

import com.resolveflow.dto.complaint.ComplaintHistoryDTO;
import com.resolveflow.dto.complaint.ComplaintRequestDTO;
import com.resolveflow.dto.complaint.ComplaintResponseDTO;
import com.resolveflow.dto.complaint.ComplaintSearchDTO;
import com.resolveflow.dto.complaint.ComplaintStatusDTO;

import java.util.List;

public interface ComplaintService {

    ComplaintResponseDTO raiseComplaint(ComplaintRequestDTO requestDTO);

    ComplaintResponseDTO getComplaintById(Long id);

    List<ComplaintResponseDTO> getAllComplaints();

    List<ComplaintHistoryDTO> getComplaintHistory(Long customerId);

    List<ComplaintResponseDTO> searchComplaints(ComplaintSearchDTO searchDTO);

    ComplaintResponseDTO updateComplaint(Long id, ComplaintRequestDTO requestDTO);

    ComplaintResponseDTO updateComplaintStatus(ComplaintStatusDTO statusDTO);

    void deleteComplaint(Long id);
}