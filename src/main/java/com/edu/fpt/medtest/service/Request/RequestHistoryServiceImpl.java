package com.edu.fpt.medtest.service.Request;

import com.edu.fpt.medtest.entity.RequestHistory;
import com.edu.fpt.medtest.repository.RequestHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RequestHistoryServiceImpl implements RequestHistoryService {
    @Autowired
    private RequestHistoryRepository requestHistoryRepository;

    @Override
    public void save(RequestHistory requestHistory) {
        requestHistoryRepository.save(requestHistory);
    }

    @Override
    public List<RequestHistory> listRecentStatus(String requestID) {
        List<RequestHistory> lsStatus = requestHistoryRepository.findByRequestIDOrderByCreatedTimeDesc(requestID);
        return lsStatus;
    }

    /*@Override
    public List<RequestHistory> getAllByUserIDAndStatus(int userID, String status) {
        List<RequestHistory> requestByUserIDAndStatus = requestHistoryRepository.findByRequestIDAndStatusOrderByCreatedTimeDesc(userID, status);
        return requestByUserIDAndStatus;
    }*/


}
