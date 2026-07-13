package com.study.tdd.application;

import java.util.UUID;

import com.study.tdd.application.command.CreateShopperCommand;
import com.study.tdd.domain.Shopper;
import com.study.tdd.infrastructure.persistence.ShopperRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.study.tdd.domain.validation.UserPropertyValidator.isEmailValid;
import static com.study.tdd.domain.validation.UserPropertyValidator.isPasswordValid;
import static com.study.tdd.domain.validation.UserPropertyValidator.isUsernameValid;

@Service
public class ShopperSignUpService {

    private final PasswordEncoder passwordEncoder;
    private final ShopperRepository repository;

    @Autowired
    public ShopperSignUpService(
            PasswordEncoder passwordEncoder,
            ShopperRepository repository
    ) {
        this.passwordEncoder = passwordEncoder;
        this.repository = repository;
    }

    public void signUp(CreateShopperCommand command) {
        if (!isCommandValid(command)) {
            throw new InvalidCommandException();
        }

        repository.save(createShopper(command));
    }

    private Shopper createShopper(CreateShopperCommand command) {
        var shopper = new Shopper();
        shopper.setId(UUID.randomUUID());
        shopper.setEmail(command.email());
        shopper.setUsername(command.username());
        shopper.setHashedPassword(passwordEncoder.encode(command.password()));
        return shopper;
    }

    private static boolean isCommandValid(CreateShopperCommand command) {
        return isEmailValid(command.email())
                && isUsernameValid(command.username())
                && isPasswordValid(command.password());
    }
}
