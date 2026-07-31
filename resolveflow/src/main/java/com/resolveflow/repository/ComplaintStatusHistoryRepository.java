package com.resolveflow.repository;

import com.resolveflow.entity.ComplaintStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComplaintStatusHistoryRepository extends JpaRepository<ComplaintStatusHistory, Long> {
}