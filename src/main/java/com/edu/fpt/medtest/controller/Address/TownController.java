package com.edu.fpt.medtest.controller.Address;

import com.edu.fpt.medtest.entity.Town;
import com.edu.fpt.medtest.exception.ResourceNotFoundException;
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
        List<Town> townList = townService.listTown();
        if (townList.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(true, "Không có phường/xã trong hệ thống!"), HttpStatus.OK);
        }
        List<Town> returnTown = new ArrayList<>();
        for (Town town : townList.subList(1, townList.size())) {
            returnTown.add(town);
        }
        if (returnTown.isEmpty()){
            return new ResponseEntity<>(new ApiResponse(true, "Không có phường/xã nào!"), HttpStatus.OK);
        }
        return new ResponseEntity<>(returnTown, HttpStatus.OK);
    }

    //create new town
    @PostMapping("/create")
    public ResponseEntity<?> createNewTown(@RequestBody Town town) {
        List<Town> lsTown = townService.listTown();
        for (Town townTracking : lsTown) {
            if (town.getTownCode().equalsIgnoreCase(townTracking.getTownCode())) {
                return new ResponseEntity<>(new ApiResponse(true, "Không tồn tại code phường/xã"), HttpStatus.OK);
            }
        }
        townService.saveTown(town);
        return new ResponseEntity<>(new ApiResponse(true, "Tạo phường/xã mới thành công!"), HttpStatus.OK);
    }

    //View detail of 1 town
    @GetMapping(value = "/detail/{code}")
    public ResponseEntity<?> getTown(@PathVariable("code") String code) {
        /*Optional<Town> getTown = Optional.ofNullable(townService.getTownByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Town", "townCode", code)));*/
        Optional<Town> getTown = townService.getTownByCode(code);
        if (!getTown.isPresent()){
            return new ResponseEntity<>(new ApiResponse(true,"Không tồn tại code phường/xã"),HttpStatus.OK);
        }
        return new ResponseEntity<>(getTown, HttpStatus.OK);
    }

    //update 1 town detail
    @PutMapping(value = "/detail/update/{code}")
    public ResponseEntity<?> updateTown(@RequestBody Town town, @PathVariable("code") String code) {
        /*Optional<Town> getTown = Optional.ofNullable(townService.getTownByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Town", "TownCode", code)));*/
        Optional<Town> getTown = townService.getTownByCode(code);
        if (!getTown.isPresent()){
            return new ResponseEntity<>(new ApiResponse(true,"Không tồn tại code phường/xã"),HttpStatus.OK);
        }
        town.setTownCode(code);
        townService.saveTown(town);
        return new ResponseEntity<>(new ApiResponse(true, "Cập nhật phường/xã mới thành công!"), HttpStatus.OK);
    }
}
