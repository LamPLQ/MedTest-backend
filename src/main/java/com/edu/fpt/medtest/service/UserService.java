package com.edu.fpt.medtest.service;

import com.edu.fpt.medtest.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public interface UserService {
    List<User> getListUser();
    void saveUser(User user);
    Optional<User> findUserByID(int id);
}
