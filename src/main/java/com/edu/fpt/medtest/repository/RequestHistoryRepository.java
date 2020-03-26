package com.edu.fpt.medtest.repository;

import com.edu.fpt.medtest.entity.RequestHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestHistoryRepository extends JpaRepository<RequestHistory, Integer> {
    List<RequestHistory> findByRequestIDOrderByCreatedTimeDesc(int id);

    List<RequestHistory> findByRequestIDAndStatusOrderByCreatedTimeDesc(int RequestID, String status);

    List<RequestHistory> findAllByRequestIDOrderByCreatedTimeDesc(int requestID);

    boolean existsByRequestID(int requestID);

    List<RequestHistory> findAllByRequestIDAndStatusAndUserIDOrderByCreatedTimeDesc(int requestID, String status, int userID);

    boolean existsByRequestIDAndStatusAndUserID(int requestID, String status, int userID);
}

