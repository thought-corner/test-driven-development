package com.study.tdd.support;

import java.util.UUID;

public class EmailGenerator {

    public static String generateEmail() {
        return UUID.randomUUID() + "@test.com";
    }
}
