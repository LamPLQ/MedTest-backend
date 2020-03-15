package com.edu.fpt.medtest.service;

import com.edu.fpt.medtest.entity.User;
import com.edu.fpt.medtest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
    private UserRepository userRepository;

    @Override
    public List<User> getListUser() {
        List<User> listUser = (List<User>) userRepository.findAll();
        return listUser;
    }
    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public Optional<User> findUserByID(int id) {
        Optional<User> getUserByID = userRepository.findById(id);
        return getUserByID;
    }
}
