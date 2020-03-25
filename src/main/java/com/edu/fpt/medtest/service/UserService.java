package com.edu.fpt.medtest.service;

import com.edu.fpt.medtest.entity.User;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;


@Service
public interface UserService {
    List<User> getListUser();

    void saveUser(User user);

    Optional<User> findUserByID(int id);

    void update(User user);

    List<User> lsUserActive();

    List<User> lsUserNotActive();

    List<User> lsUserByDistrict(String districtCode);

    List<User> lsUserByTown(String townCode);

    void resetPassword(User user);
}
