package com.edu.fpt.medtest.controller;

import com.edu.fpt.medtest.entity.Town;
import com.edu.fpt.medtest.exception.ResourceNotFoundException;
import com.edu.fpt.medtest.service.TownService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/management/districts/towns")
public class TownController {

    @Autowired
    private TownService townService;

    //get all town by district
    @GetMapping("/list")
    public ResponseEntity<?> getAllTown() {
        List<Town> townList = townService.listTown();
        if (townList.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(true, "There is no town"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(townList, HttpStatus.OK);
    }

    //create new town
    @PostMapping("/create")
    public ResponseEntity<?> createNewTown(@RequestBody Town town) {
        List<Town> lsTown = townService.listTown();
        for (Town townTracking : lsTown) {
            if (town.getTownCode().equalsIgnoreCase(townTracking.getTownCode())) {
                return new ResponseEntity<>(new ApiResponse(false, "Already have this code"), HttpStatus.NOT_FOUND);
            }
        }
        townService.saveTown(town);
        return new ResponseEntity<>(new ApiResponse(true, "Successfully create town"), HttpStatus.OK);
    }

    //View detail of 1 town
    @GetMapping(value = "/detail/{code}")
    public ResponseEntity<?> getTown(@PathVariable("code") String code) {
        Optional<Town> getTown = Optional.ofNullable(townService.getTownByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Town", "townCode", code)));
        return new ResponseEntity<>(getTown, HttpStatus.OK);
    }

    //update 1 town detail
    @PutMapping(value = "/detail/update/{code}")
    public ResponseEntity<?> updateTown(@RequestBody Town town, @PathVariable("code") String code) {
        Optional<Town> getTown = Optional.ofNullable(townService.getTownByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Town", "TownCode", code)));
        town.setTownCode(code);
        townService.saveTown(town);
        return new ResponseEntity<>(new ApiResponse(true, "Update town successfully"), HttpStatus.OK);
    }
}
