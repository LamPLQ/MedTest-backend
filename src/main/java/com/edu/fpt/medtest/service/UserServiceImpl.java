package com.edu.fpt.medtest.service;

import com.edu.fpt.medtest.entity.User;
import com.edu.fpt.medtest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    // list all user
    @Override
    public List<User> getListUser() {
        List<User> listUser = (List<User>) userRepository.findAll();
        return listUser;
    }

    //save user
    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public Optional<User> findUserByID(int id) {
        Optional<User> getUserByID = userRepository.findById(id);
        return getUserByID;
    }

    //update a user
    @Override
    public void update(User user) {
        User userByID = userRepository.findById(user.getId()).get();
        userByID.setName(user.getName());
        userByID.setDob(user.getDob());
        userByID.setAddress(user.getAddress());
        userByID.setEmail(user.getEmail());
        userByID.setGender(user.getGender());
        userByID.setTownCode(user.getTownCode());
        userByID.setDistrictCode(user.getDistrictCode());
        userRepository.save(userByID);
    }

    @Override
    public List<User> lsUserActive() {
        List<User> lsUserActive = userRepository.findAllByActive(1);
        return lsUserActive;
    }

    @Override
    public List<User> lsUserNotActive() {
        List<User> lsUserNotActive = userRepository.findAllByActive(0);
        return lsUserNotActive;
    }

    @Override
    public List<User> lsUserByDistrict(String districtCode) {
        List<User> lsUsersByDistrictCode = userRepository.findAllByDistrictCode(districtCode);
        return lsUsersByDistrictCode;
    }

    @Override
    public List<User> lsUserByTown(String townCode) {
        List<User> lsUserByTownCode = userRepository.findAllByTownCode(townCode);
        return lsUserByTownCode;
    }

    @Override
    public void resetPassword(User user)   {
        User userResetPasswordByID = userRepository.findById(user.getId()).get();
        userResetPasswordByID.setPassword(user.getPassword());
        userRepository.save(userResetPasswordByID);
    }


}
