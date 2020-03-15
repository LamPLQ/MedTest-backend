package com.edu.fpt.medtest.controller;

import com.edu.fpt.medtest.entity.Town;
import com.edu.fpt.medtest.repository.TownRespository;
import com.edu.fpt.medtest.service.TownService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/management/districts/")
public class TownController {

    @Autowired
    private TownService townService;

    @GetMapping("/towns/list")
    public ResponseEntity<List<Town>> getAllTown(){
        List<Town> towns = townService.getListTown();
        return new ResponseEntity<>(towns, HttpStatus.OK);
    }

}
