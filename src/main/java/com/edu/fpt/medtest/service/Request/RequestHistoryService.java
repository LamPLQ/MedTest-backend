package com.edu.fpt.medtest.service.Request;

import com.edu.fpt.medtest.entity.RequestHistory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RequestHistoryService {

    void save(RequestHistory requestHistory);

    List<RequestHistory> listRecentStatus(int requestID);

    List<RequestHistory> getAllByUserIDAndStatus(int userID, String status);

}
