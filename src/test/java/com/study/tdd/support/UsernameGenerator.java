package com.study.tdd.support;

import java.util.UUID;

public class UsernameGenerator {

    public static String generateUsername() {
        return "username" + UUID.randomUUID();
    }
}
