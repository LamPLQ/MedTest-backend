package com.edu.fpt.medtest.controller.Users;

import com.edu.fpt.medtest.entity.User;
import com.edu.fpt.medtest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.NoSuchAlgorithmException;

import static com.edu.fpt.medtest.utils.EncodePassword.getSHA;
import static com.edu.fpt.medtest.utils.EncodePassword.toHexString;

public class UserController {

    @Autowired
    private UserRepository userRepository;

    //login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) throws NoSuchAlgorithmException {
        boolean login = false;
        User userLogin = userRepository.getUserByPhoneNumber(user.getPhoneNumber());
        User returnUser = new User();
        if (userLogin.getPassword().equals(toHexString(getSHA(user.getPassword())))) {
            login = true;
//            returnUser = userRepository.save(user);
//        }else returnUser = userRepository.save(null);
        }
        return new ResponseEntity<>(login, HttpStatus.OK);
    }
}
