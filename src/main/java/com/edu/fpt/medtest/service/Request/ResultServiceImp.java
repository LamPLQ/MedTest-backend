package com.edu.fpt.medtest.service.Request;


import com.edu.fpt.medtest.entity.Result;
import com.edu.fpt.medtest.repository.ResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResultServiceImp implements ResultService  {

    @Autowired
    ResultRepository resultRepository;

    @Override
    public List<Result> lsResultByRequestID(int requestID) {
        List<Result> lsResultByRequestID = resultRepository.getAllByRequestID(requestID);
        return lsResultByRequestID;
    }

    @Override
    public void saveResult(Result result) {
        resultRepository.save(result);
    }
}
