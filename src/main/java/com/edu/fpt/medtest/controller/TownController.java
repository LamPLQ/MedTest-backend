package com.edu.fpt.medtest.controller;

import com.edu.fpt.medtest.entity.District;
import com.edu.fpt.medtest.entity.Town;
import com.edu.fpt.medtest.repository.TownRespository;
import com.edu.fpt.medtest.service.DistrictService;
import com.edu.fpt.medtest.service.TownService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/management/districts/")
public class TownController {

    @Autowired
    private TownService townService;

    //get all town by district
    @GetMapping("/towns/list")
    public ResponseEntity<List<Town>> getAllTown(){
        List<Town> townList = townService.getListTown();
        return new ResponseEntity<>(townList, HttpStatus.OK);
    }

}
