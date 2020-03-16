package com.edu.fpt.medtest.repository;

import com.edu.fpt.medtest.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;

@Repository
public interface UserRepository extends CrudRepository<User,Integer> {
//    @Transactional
//    @Modifying
//    @Query("UPDATE medtestdb.user u\n" +
//            "SET Name = :name, DOB = :dob, Address = :address, Email = :email, Gender = :gender ,  townCode = :townCode, districtCode = :districtCode\n" +
//            "WHERE u.ID=:id")
//
//    int updateUser(@Param("name") String Name
//                 , @Param("dob") Date dob
//                 , @Param("address") String address
//                 , @Param("email") String email
//                 , @Param("gender") int gender
//                 , @Param("townCode") String townCode
//                 , @Param("districtCode") String districtCode
//                 , @Param("id") int id);
}
