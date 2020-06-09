package com.changgou.test;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Test;

import java.util.Date;
import java.util.UUID;

public class TestJWT {
    private final static String keywords = "itcast";
    
    @Test
    public void testCreateJWT(){

        JwtBuilder jwtBuilder = Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setSubject("传智播客，黑马程序员")
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, keywords);

        System.out.println(jwtBuilder.compact());
    }


    @Test
    public void testJWTparser(){
        String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIzODQ0MTAzZi0zMjA0LTQ4YmUtOGExZS0wNDhlM2EzNzRjZDciLCJzdWIiOiLkvKDmmbrmkq3lrqLvvIzpu5HpqaznqIvluo_lkZgiLCJpYXQiOjE1OTA2MzU0MjR9.Nss_8xwvsAMpXD5-1Y9QIRHjJ28r9N8UFWmYwG8vHio";
        Claims claims = Jwts.parser().setSigningKey(keywords).parseClaimsJws(jwt).getBody();
        System.out.println(claims);
    }










}
