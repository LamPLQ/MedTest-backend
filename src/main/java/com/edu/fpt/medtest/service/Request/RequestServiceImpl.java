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
        //List<Request> lsRequest = requestRepository.getAllByOrderByCreatedTimeDesc();
        return lsRequest;
    }

    @Override
    public Request getRequest(String requestID) {
        Request requestByID = requestRepository.getByRequestID(requestID);
        return requestByID;
    }

    @Override
    public void saveRequest(Request request) {
        requestRepository.save(request);
    }

    @Override
    public List<Request> getListByUser(int userID) {
        List<Request> getListRequestByUserID = requestRepository.getAllByUserIDOrderByCreatedTimeDesc(userID);
        return getListRequestByUserID;
    }

    @Override
    public List<Request> lsRequestByCreatedTimeDesc() {
        List<Request> lsRequest = requestRepository.getAllByOrderByCreatedTimeDesc();
        return lsRequest;
    }

    @Override
    public List<Request> lsRequestByMeetingTime() {
        List<Request> lsRequest = requestRepository.getAllByOrderByMeetingTimeAsc();
        return lsRequest;
    }


}
