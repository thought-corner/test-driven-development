package com.study.tdd.application.command;

public record CreateSellerCommand(
    String email,
    String username,
    String password,
    String contactEmail
) {
}
