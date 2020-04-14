package com.edu.fpt.medtest.repository;

import com.edu.fpt.medtest.entity.RequestHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestHistoryRepository extends JpaRepository<RequestHistory, Integer> {
    List<RequestHistory> findByRequestIDOrderByCreatedTimeDesc(String id);

    List<RequestHistory> findByRequestIDAndStatusOrderByCreatedTimeDesc(String RequestID, String status);

    List<RequestHistory> findAllByRequestIDOrderByCreatedTimeDesc(String requestID);

    boolean existsByRequestID(String requestID);

    List<RequestHistory> findAllByRequestIDAndStatusAndUserIDOrderByCreatedTimeDesc(String requestID, String status, int userID);

    boolean existsByRequestIDAndStatusAndUserID(String requestID, String status, int userID);
}

