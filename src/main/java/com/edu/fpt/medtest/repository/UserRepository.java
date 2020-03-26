package com.edu.fpt.medtest.repository;

import com.edu.fpt.medtest.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    User getUserByPhoneNumber(String phoneNumber);

    User getUserByPhoneNumberAndRole(String phoneNumber, String role);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByPhoneNumberAndRole(String phoneNumber, String role);

    List<User> findAllByRole(String role);

    List<User> findAllByActive(int active);

    List<User> findAllByDistrictCode(String districtCode);

    List<User> findAllByTownCode(String townCode);

    User getUserByPhoneNumberAndEmailAndRole(String phoneNumber, String email, String role);

}
