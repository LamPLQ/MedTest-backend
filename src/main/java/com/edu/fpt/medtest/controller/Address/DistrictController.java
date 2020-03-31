package com.edu.fpt.medtest.controller.Address;

import com.edu.fpt.medtest.service.Address.TownService;
import com.edu.fpt.medtest.utils.ApiResponse;
import com.edu.fpt.medtest.entity.District;
import com.edu.fpt.medtest.entity.Town;
import com.edu.fpt.medtest.exception.ResourceNotFoundException;
import com.edu.fpt.medtest.model.DistrictModel;
import com.edu.fpt.medtest.repository.DistrictRepository;
import com.edu.fpt.medtest.repository.TownRepository;
import com.edu.fpt.medtest.service.Address.DistrictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/management/districts")
public class DistrictController {

    @Autowired
    private DistrictService districtService;

    /*@Autowired
    private DistrictRepository districtRepository;

    @Autowired
    private TownRepository townRepository;*/

    @Autowired
    private TownService townService;

    // Get all district
    @GetMapping("/list")
    public ResponseEntity<?> list() {
        List<District> listDistrict = districtService.listDistrict();
        if (listDistrict.isEmpty()) {
            //return new ResponseEntity<>(new ApiResponse(true, "There is no district"), HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(new ApiResponse(false, "NO_DISTRICT"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(listDistrict, HttpStatus.OK);
    }

    //Add new district
    @PostMapping("/create")
    public ResponseEntity<?> addDistrict(@RequestBody District district) {
        List<District> lsDistrict = districtService.listDistrict();
        for (District districtTrack : lsDistrict) {
            if (district.getDistrictCode().equalsIgnoreCase(districtTrack.getDistrictCode()))
                //return new ResponseEntity<>(new ApiResponse(false, "Already have this code"), HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>(new ApiResponse(false, "CODE_EXISTED"), HttpStatus.BAD_REQUEST);
        }
        districtService.saveDistrict(district);
        //return new ResponseEntity<>(new ApiResponse(true, "Successfully create district"), HttpStatus.OK);
        return new ResponseEntity<>(new ApiResponse(true, "SUCCEED_CREATED"), HttpStatus.OK);
    }

    //View detail of 1 district
    @GetMapping(value = "/detail/{code}")
    public ResponseEntity<?> getDistrict(@PathVariable("code") String code) {
        /*Optional<District> getDistrict = Optional.ofNullable(districtService.findDistrictByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("DistrictModel", "DistrictCode", code)));
        */
        Optional<District> getDistrict = districtService.findDistrictByCode(code);
        if(!getDistrict.isPresent()){
            return new ResponseEntity<>(new ApiResponse(false,"NOT_EXIST_DISTRICT"),HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(getDistrict, HttpStatus.OK);
    }

    //update 1 district detail
    @PutMapping(value = "/detail/update/{code}")
    public ResponseEntity<?> updateDistrict(@RequestBody District district, @PathVariable("code") String code) {
        Optional<District> getDistrict = Optional.ofNullable(districtService.findDistrictByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("DistrictModel", "DistrictCode", code)));
        district.setDistrictCode(code);
        districtService.saveDistrict(district);
        return new ResponseEntity<>(new ApiResponse(true, "Update district successfully"), HttpStatus.OK);
    }

    //get List district-town by DistrictID
    @GetMapping("/district-town-list")
    public ResponseEntity<?> listDistrict() {
        List<DistrictModel> lsResult = new ArrayList<>();
        //List<District> list = districtRepository.findAll();
        List<District> list = districtService.listDistrict();
        for (District district : list) {
            //List<Town> townList = townRepository.getAllByDistrictCode(district.getDistrictCode());
            List<Town> townList = townService.getAllByDistrictCode(district.getDistrictCode());
            DistrictModel districtModel = new DistrictModel();
            districtModel.setDistrictCode(district.getDistrictCode());
            districtModel.setDistrictName(district.getDistrictName());
            districtModel.setListTown(townList);
            lsResult.add(districtModel);
        }
        return new ResponseEntity<>(lsResult, HttpStatus.OK);
    }


}
