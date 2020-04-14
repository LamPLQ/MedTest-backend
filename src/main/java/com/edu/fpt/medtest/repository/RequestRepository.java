package com.edu.fpt.medtest.repository;

import com.edu.fpt.medtest.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Integer> {
    boolean existsByRequestID(String requestID);

    List<Request> getAllByUserIDOrderByCreatedTimeDesc(int userID);

    Request getByRequestID(String requestID);
}
