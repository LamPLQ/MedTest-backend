package com.edu.fpt.medtest.repository;

import com.edu.fpt.medtest.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Integer> {
    boolean existsByRequestID(int requestID);

    List<Request> getAllByUserIDOrderByCreatedDateDesc(int userID);
}
