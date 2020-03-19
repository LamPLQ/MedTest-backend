package com.edu.fpt.medtest.controller.Users;

import com.edu.fpt.medtest.entity.District;
import com.edu.fpt.medtest.entity.Town;
import com.edu.fpt.medtest.entity.User;
import com.edu.fpt.medtest.repository.DistrictRepository;
import com.edu.fpt.medtest.repository.TownRepository;
import com.edu.fpt.medtest.repository.UserRepository;
import com.edu.fpt.medtest.service.UserService;
import com.edu.fpt.medtest.utils.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

import static com.edu.fpt.medtest.utils.EncodePassword.getSHA;
import static com.edu.fpt.medtest.utils.EncodePassword.toHexString;

@Service
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private DistrictRepository districtRepository;

    @Autowired
    private TownRepository townRepository;

    //Login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) throws NoSuchAlgorithmException {
        boolean login = false;
        User userLogin = userRepository.getUserByPhoneNumber(user.getPhoneNumber());
        if (userLogin.getPassword().equals(toHexString(getSHA(user.getPassword())))) {
            login = true;
        }
        return new ResponseEntity<>(login, HttpStatus.OK);
    }

    // List user with state ACTIVE
    @GetMapping("/list/active")
    public ResponseEntity<?> listActive(){
        List<User> lsUsersActive = userService.lsUserActive();
        if (lsUsersActive.isEmpty()){
            return new ResponseEntity<>(new ApiResponse(true, "There is no user active"),HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(lsUsersActive, HttpStatus.OK);
    }


    // List user with state DE-ACTIVE
    @GetMapping("/list/deactive")
    public ResponseEntity<?> listDeactive(){
        List<User> lsUsersDeactive = userService.lsUserNotActive();
        if (lsUsersDeactive.isEmpty()){
            return new ResponseEntity<>(new ApiResponse(true, "There is no user deactive"),HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(lsUsersDeactive, HttpStatus.OK);
    }

    //List user in 1 district
    @GetMapping("/list/district/{code}")
    public ResponseEntity<?> listDistrict(@PathVariable("code") String code){
        List<User> lsByDistrict = userService.lsUserByDistrict(code);
        Optional<District> district = districtRepository.findById(code);
        if(lsByDistrict.isEmpty())
            return new ResponseEntity<>(new ApiResponse(true, "There is no user in district " + district.get().getDistrictName()), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(lsByDistrict,HttpStatus.OK);
    }

    //List user in 1 town
    @GetMapping("/list/town/{code}")
    public ResponseEntity<?> listTown(@PathVariable("code") String code){
        List<User> lsByTown = userService.lsUserByTown(code);
        Optional<Town> town = townRepository.findById(code);
        if(lsByTown.isEmpty())
            return new ResponseEntity<>(new ApiResponse(true, "There is no user in district " + town.get().getTownName()), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(lsByTown,HttpStatus.OK);
    }

    //Reset password for user
    @PostMapping("/reset-password/{id}")
    public ResponseEntity<?> resetPassword(@PathVariable("id") int id) throws NoSuchAlgorithmException {
        Optional<User> getUserById = userRepository.findById(id);
        if (!getUserById.isPresent()){
            return new ResponseEntity<>(new ApiResponse(true, "There is no user with id"), HttpStatus.NOT_FOUND);
        }
        getUserById.get().setId(id);
        getUserById.get().setPassword(toHexString(getSHA("medtest2020")));
        userService.resetPassword(getUserById.get());
        return new ResponseEntity<>(
                new ApiResponse(true,"Reset password for user " + getUserById.get().getName() + "with password: medtest2020"), HttpStatus.OK);
    }
}


