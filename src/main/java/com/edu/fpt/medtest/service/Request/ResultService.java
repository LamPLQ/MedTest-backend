package com.edu.fpt.medtest.service.Request;

import com.edu.fpt.medtest.entity.Result;
import org.springframework.stereotype.Service;

import java.util.List;



@Service
public interface ResultService {
    List<Result> lsResultByRequestID(int requestID);

    void saveResult(Result result);
}
