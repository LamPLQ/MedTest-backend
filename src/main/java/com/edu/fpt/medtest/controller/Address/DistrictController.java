package com.edu.fpt.medtest.controller.Address;

import com.edu.fpt.medtest.entity.District;
import com.edu.fpt.medtest.entity.Town;
import com.edu.fpt.medtest.model.DistrictModel;
import com.edu.fpt.medtest.service.Address.DistrictService;
import com.edu.fpt.medtest.service.Address.TownService;
import com.edu.fpt.medtest.utils.ApiResponse;
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

    @Autowired
    private TownService townService;

    // Get all district
    @GetMapping("/list")
    public ResponseEntity<?> list() {
        try {
            List<District> listDistrict = districtService.listDistrict();
            if (listDistrict.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse(true, "Không có quận/huyện trong hệ thống!"), HttpStatus.OK);
            }
            List<District> returnList = new ArrayList<>();
            for (District district : listDistrict.subList(1, listDistrict.size())) {
                returnList.add(district);
            }
            if (returnList.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse(true, "Không có quận/huyện nào!"), HttpStatus.OK);
            }
            return new ResponseEntity<>(returnList, HttpStatus.OK);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

    //Add new district
    @PostMapping("/create")
    public ResponseEntity<?> addDistrict(@RequestBody District district) {
        try {
            List<District> lsDistrict = districtService.listDistrict();
            for (District districtTrack : lsDistrict) {
                if (district.getDistrictCode().equalsIgnoreCase(districtTrack.getDistrictCode()))
                    return new ResponseEntity<>(new ApiResponse(true, "Code quận/huyện bị trùng!"), HttpStatus.OK);
            }
            districtService.saveDistrict(district);
            return new ResponseEntity<>(new ApiResponse(true, "Tạo 1 quận/huyện mới thành công"), HttpStatus.OK);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

    //View detail of 1 district
    @GetMapping(value = "/detail/{code}")
    public ResponseEntity<?> getDistrict(@PathVariable("code") String code) {
        try {
            Optional<District> getDistrict = districtService.findDistrictByCode(code);
            if (!getDistrict.isPresent()) {
                return new ResponseEntity<>(new ApiResponse(true, "Không tồn tại quận/huyện!"), HttpStatus.OK);
            }
            return new ResponseEntity<>(getDistrict, HttpStatus.OK);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

    //update 1 district detail
    @PutMapping(value = "/detail/update/{code}")
    public ResponseEntity<?> updateDistrict(@RequestBody District district, @PathVariable("code") String code) {
        try {
            Optional<District> getDistrict = districtService.findDistrictByCode(code);
            if (!getDistrict.isPresent()) {
                return new ResponseEntity<>(new ApiResponse(true, "Không tồn tại code quận/huyện!"), HttpStatus.OK);
            }
            district.setDistrictCode(code);
            districtService.saveDistrict(district);
            return new ResponseEntity<>(new ApiResponse(true, "Cập nhật quận/huyện thành công!"), HttpStatus.OK);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

    //get List district-town by DistrictID
    @GetMapping("/district-town-list")
    public ResponseEntity<?> listDistrict() {
        try {
            List<DistrictModel> lsResult = new ArrayList<>();
            List<District> list = districtService.listDistrict();
            for (District district : list.subList(1, list.size())) {
                List<Town> townList = townService.getAllByDistrictCode(district.getDistrictCode());
                DistrictModel districtModel = new DistrictModel();
                districtModel.setDistrictCode(district.getDistrictCode());
                districtModel.setDistrictName(district.getDistrictName());
                districtModel.setListTown(townList);
                lsResult.add(districtModel);
            }
            return new ResponseEntity<>(lsResult, HttpStatus.OK);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

    //get all town in 1 district
    @GetMapping("/{districtCode}/list-town")
    public ResponseEntity<?> lsTownByDistrict(@PathVariable("districtCode") String districtCode) {
        try {
            Optional<District> getDistrictByDistricCode = districtService.findDistrictByCode(districtCode);
            if (!getDistrictByDistricCode.isPresent()) {
                return new ResponseEntity<>(new ApiResponse(true, "Không tồn tại mã quận " + districtCode), HttpStatus.OK);
            }
            List<Town> lsAllTownByDistrict = townService.getAllByDistrictCode(districtCode);
            if (lsAllTownByDistrict.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse(true, "Không có phường/xã nào thuộc quận này!"), HttpStatus.OK);
            }
            return new ResponseEntity<>(lsAllTownByDistrict, HttpStatus.OK);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

}
