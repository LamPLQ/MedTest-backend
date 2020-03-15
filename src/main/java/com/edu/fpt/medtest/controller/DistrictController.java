package com.edu.fpt.medtest.controller;

import com.edu.fpt.medtest.entity.District;
import com.edu.fpt.medtest.exception.ResourceNotFoundException;
import com.edu.fpt.medtest.repository.DistrictRepository;
import com.edu.fpt.medtest.service.DistrictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/management/districts")
public class DistrictController {

    @Autowired
    private DistrictService districtService;

    // Get all district
    @GetMapping("/list")
    public ResponseEntity<?> list() {
        List<District> listDistrict = districtService.getListDistrict();
        return new ResponseEntity<>(listDistrict, HttpStatus.OK);
    }

    //Add new district
    @PostMapping("/add")
    public ResponseEntity<?> addDistrict(@RequestBody District district) {
        List<District> lsDistrict = districtService.getListDistrict();
        for (District districtTrack : lsDistrict) {
            if (district.getDistrictCode().equalsIgnoreCase(districtTrack.getDistrictCode()))
                return new ResponseEntity<>(new ApiResponse(false, "Already have this code"), HttpStatus.BAD_REQUEST);
        }
        districtService.saveDistrict(district);
        return new ResponseEntity<>(new ApiResponse(true, "Successfully registered district"), HttpStatus.OK);
    }

    //View detail of 1 district
    @GetMapping(value = "/detail/{code}")
    public ResponseEntity<?> getDistrict(@PathVariable("code") String code) {
        Optional<District> getDistrict = Optional.ofNullable(districtService.findDistrictByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("District", "DistrictCode", code)));
        return new ResponseEntity<>(getDistrict,HttpStatus.OK);
    }

    //delete 1 district
    @DeleteMapping(value = "/detail/delete/{code}")
    public ResponseEntity<?> deleteDistrict(@PathVariable("code") String code){
        Optional<District> getDistrict = districtService.findDistrictByCode(code);
        System.out.println(getDistrict);
        if (!getDistrict.isPresent())
            return new ResponseEntity<>(new ApiResponse(false,"DistrictCode is not available!"),HttpStatus.BAD_REQUEST);
        else
            districtService.deleteDistrict(code);
        return new ResponseEntity<>(new ApiResponse(true,"Delete district successfully!"),HttpStatus.OK);
    }

    //update 1 district detail
    @PutMapping(value = "/detail/update/{code}")
    public ResponseEntity<?> updateDistrict(@RequestBody District district,@PathVariable("code") String code){
        Optional<District> getDistrict = Optional.ofNullable(districtService.findDistrictByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("District", "DistrictCode", code)));
        district.setDistrictCode(code);
        districtService.saveDistrict(district);
        return new ResponseEntity<>(new ApiResponse(true,"Update district successfully"), HttpStatus.OK);
    }



}
