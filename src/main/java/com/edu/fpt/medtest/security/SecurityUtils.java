package com.edu.fpt.medtest.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Date;
import java.util.stream.Collectors;

public class SecurityUtils {
    public static final String SECRET = "SecretKeyToGenJWTs";
    public static final long EXPIRATION_TIME = 864_000_000; // 10 days
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String CUSTOMER_REGISTER = "/users/customers/register";
    public static final String WEB_LOGIN = "/users/login";
    public static final String NURSE_REGISTER = "/users/nurses/register";
    public static final String COORDINATOR_REGISTER = "/users/coordinators/register";
    public static final String CUSTOMER_LOGIN = "/users/customers/login";
    public static final String NURSE_LOGIN = "/users/nurses/login";
    public static final String COORDINATOR_LOGIN = "/users/coordinators/login";
    public static final String FORGOT_PASSWORD = "/users/forgot-password";
    public static final String LIST_ARTICLE = "/articles/list";
    public static final String DETAIL_ARTICLE = "/articles/detail/{id}";
    public static final String LIST_TESTTYPE_TEST = "/test-types/type-test";
    public static final String LIST_DISTRICT = "/management/districts/list";
    public static final String DETAIL_DISTRICT= "/management/districts/detail/{id}";
    public static final String LIST_DISTRICT_TOWN = "/management/districts/district-town-list";
    public static final String LIST_TOWN = "/management/districts/towns/list";
    public static final String DETAIL_TOWN = "/management/districts/towns/detail/{id}";
    public static final String DETAIL_TEST = "/test-types/tests/detail/{id}";
    public static final String LIST_TEST = "/test-types/tests/list";
    public static final String VERIFY_PHONE_BY_SEND_OTP = "/users/send-otp";
    public static final String VALID_PHONE_OTP = "/users/valid-phone-otp";
    public static final String RESEND_OTP = "/users/resend-otp";
    public static final String LIST_TEST_OF_LATEST_VERSION = "/tests/versions/lastest-version-test";
//
    public static final String UPLOAD_FILE = "/uploadFile";
    public static final String SAVE_FILE = "/saveFile/{fileName:.+}";


    public static String generateToken(Authentication authentication) {
        final String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        return Jwts.builder()
                .setSubject(authentication.getName())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET.getBytes())
                .compact();
    }
}