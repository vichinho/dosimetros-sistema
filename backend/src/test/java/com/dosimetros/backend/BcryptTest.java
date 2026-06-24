package com.dosimetros.backend;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BcryptTest {

    @Test
    void generarHash() {
        String rawPassword = "dosimetria";
        String hash = new BCryptPasswordEncoder().encode(rawPassword);
        System.out.println("PASSWORD PLANA: " + rawPassword);
        System.out.println("HASH BCRYPT: " + hash);
    }
}