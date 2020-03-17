package com.edu.fpt.medtest.repository;

import com.edu.fpt.medtest.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    User getUserByPhoneNumber(String phoneNumber);
}
