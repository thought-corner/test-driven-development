package com.study.tdd.application.command;

public record CreateShopperCommand(
    String email,
    String username,
    String password
) {
}
