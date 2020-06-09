package com.changgou.test;

import org.springframework.security.crypto.bcrypt.BCrypt;

public class TestBCrypt {
    public static void main(String[] args) {
        String gensalt = BCrypt.gensalt();
        String hashpw = BCrypt.hashpw("123456", gensalt);
        System.out.println("===="+hashpw);
    }
}
