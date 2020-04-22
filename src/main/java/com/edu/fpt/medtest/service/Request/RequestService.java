package com.edu.fpt.medtest.service.Request;

import com.edu.fpt.medtest.entity.Request;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface RequestService {
    List<Request> lsRequest();

    Request getRequest(String requestID);

    void saveRequest(Request request);

    List<Request> getListByUser(int userID);

    List<Request> lsRequestByCreatedTimeDesc();
}
