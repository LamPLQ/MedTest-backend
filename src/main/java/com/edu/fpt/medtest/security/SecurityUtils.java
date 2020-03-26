package com.edu.fpt.medtest.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class SecurityUtils {
    public static final String SECRET = "SecretKeyToGenJWTs";
    public static final long EXPIRATION_TIME = 864_000_000; // 10 days
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/users/customers/register";
    public static final String USER_LOGIN = "/users/login";
    public static final String FORGOT_PASSWORD = "/users/forgot-password";
    public static final String LIST_ARTICLE = "/articles/list";
    public static final String LIST_TESTTYPE_TEST = "/test-types/type-test";
    public static final String LIST_DISTRICT = "/management/districts/list";
    public static final String DETAIL_DISTRICT= "/management/districts/detail/{id}";
    public static final String LIST_DISTRICT_TOWN = "/management/districts/district-town-list";
    public static final String LIST_TOWN = "/management/districts/towns/list";
    public static final String DETAIL_TOWN = "/management/districts/towns/detail/{id}";
    public static final String DETAIL_TEST = "/test-types/tests/detail/{id}";
    public static final String LIST_TEST = "/test-types/tests/list";
    public static final String DETAIL_ARTICLE = "/articles/detail/{id}";


    public static String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET.getBytes())
                .compact();
    }
}