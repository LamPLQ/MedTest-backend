package com.edu.fpt.medtest.repository;

import com.edu.fpt.medtest.entity.ValidPhoneToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<ValidPhoneToken, Integer> {
    Optional<ValidPhoneToken> getByPhoneNumberAndToken(String phoneNumber, String token);
}
