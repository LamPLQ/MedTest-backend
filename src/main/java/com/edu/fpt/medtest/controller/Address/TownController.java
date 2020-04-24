package com.edu.fpt.medtest.controller.Address;

import com.edu.fpt.medtest.entity.Town;
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
@RequestMapping("/management/districts/towns")
public class TownController {

    @Autowired
    private TownService townService;

    //get all town by district
    @GetMapping("/list")
    public ResponseEntity<?> getAllTown() {
        try {
            List<Town> townList = townService.listTown();
            if (townList.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse(true, "Không có phường/xã trong hệ thống!"), HttpStatus.OK);
            }
            List<Town> returnTown = new ArrayList<>();
            for (Town town : townList.subList(1, townList.size())) {
                returnTown.add(town);
            }
            if (returnTown.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse(true, "Không có phường/xã nào!"), HttpStatus.OK);
            }
            return new ResponseEntity<>(returnTown, HttpStatus.OK);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

    //create new town
    @PostMapping("/create")
    public ResponseEntity<?> createNewTown(@RequestBody Town town) {
        try {
            List<Town> lsTown = townService.listTown();
            for (Town townTracking : lsTown) {
                if (town.getTownCode().equalsIgnoreCase(townTracking.getTownCode())) {
                    return new ResponseEntity<>(new ApiResponse(true, "Không tồn tại code phường/xã"), HttpStatus.OK);
                }
            }
            townService.saveTown(town);
            return new ResponseEntity<>(new ApiResponse(true, "Tạo phường/xã mới thành công!"), HttpStatus.OK);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

    //View detail of 1 town
    @GetMapping(value = "/detail/{code}")
    public ResponseEntity<?> getTown(@PathVariable("code") String code) {
        try {
            Optional<Town> getTown = townService.getTownByCode(code);
            if (!getTown.isPresent()) {
                return new ResponseEntity<>(new ApiResponse(true, "Không tồn tại code phường/xã"), HttpStatus.OK);
            }
            return new ResponseEntity<>(getTown, HttpStatus.OK);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

    //update 1 town detail
    @PutMapping(value = "/detail/update/{code}")
    public ResponseEntity<?> updateTown(@RequestBody Town town, @PathVariable("code") String code) {
        try {
            Optional<Town> getTown = townService.getTownByCode(code);
            if (!getTown.isPresent()) {
                return new ResponseEntity<>(new ApiResponse(true, "Không tồn tại code phường/xã"), HttpStatus.OK);
            }
            town.setTownCode(code);
            townService.saveTown(town);
            return new ResponseEntity<>(new ApiResponse(true, "Cập nhật phường/xã mới thành công!"), HttpStatus.OK);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

}
