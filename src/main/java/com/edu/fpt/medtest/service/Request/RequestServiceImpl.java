package com.edu.fpt.medtest.service.Request;

import com.edu.fpt.medtest.entity.Request;
import com.edu.fpt.medtest.repository.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RequestServiceImpl implements RequestService {
    @Autowired
    private RequestRepository requestRepository;

    @Override
    public List<Request> lsRequest() {
        List<Request> lsRequest = requestRepository.findAll();
        return lsRequest;
    }

    @Override
    public Optional<Request> getRequest(int requestID) {
        Optional<Request> getRequest = requestRepository.findById(requestID);
        return getRequest;
    }

    @Override
    public void saveRequest(Request request) {
        requestRepository.save(request);
    }

    @Override
    public List<Request> getListByUser(int userID) {
        List<Request> getListRequestByUserID = requestRepository.getAllByUserIDOrderByCreatedDateDesc(userID);
        return getListRequestByUserID;
    }


}
