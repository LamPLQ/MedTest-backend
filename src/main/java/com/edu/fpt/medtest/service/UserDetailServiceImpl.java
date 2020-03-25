package com.edu.fpt.medtest.service;

import com.edu.fpt.medtest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.edu.fpt.medtest.entity.User applicationUser = userRepository.getUserByPhoneNumber(username);
        if (username == null) {
            throw new UsernameNotFoundException(username);
        }
        return new User(applicationUser.getPhoneNumber(), applicationUser.getPassword(), Collections.EMPTY_LIST) {
        };
    }
}