package com.edu.fpt.medtest.service;

import com.edu.fpt.medtest.entity.Request;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface RequestService {
    List<Request> lsRequest();
    Optional<Request> getRequest(int requestID);
    void saveRequest(Request request);
}
