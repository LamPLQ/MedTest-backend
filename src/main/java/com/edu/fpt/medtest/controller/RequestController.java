package com.edu.fpt.medtest.controller;

import com.edu.fpt.medtest.utils.ApiResponse;
import com.edu.fpt.medtest.entity.Request;
import com.edu.fpt.medtest.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/requests")
public class RequestController  {

    @Autowired
    private RequestService requestService;

    @PostMapping("/create")
    public ResponseEntity<?> createNewRequest(@RequestBody Request request) {
        requestService.saveRequest(request);
        return new ResponseEntity<>(new ApiResponse(true, "Successfully create appointment"), HttpStatus.OK);
    }
}
