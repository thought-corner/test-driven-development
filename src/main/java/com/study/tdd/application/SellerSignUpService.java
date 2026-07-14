package com.study.tdd.application;

import java.util.UUID;

import com.study.tdd.application.command.CreateSellerCommand;
import com.study.tdd.application.exception.BusinessException;
import com.study.tdd.application.exception.UserErrorCode;
import com.study.tdd.domain.Seller;
import com.study.tdd.infrastructure.persistence.SellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.study.tdd.domain.validation.UserPropertyValidator.isEmailValid;
import static com.study.tdd.domain.validation.UserPropertyValidator.isPasswordValid;
import static com.study.tdd.domain.validation.UserPropertyValidator.isUsernameValid;

@Service
public class SellerSignUpService {

    private final PasswordEncoder passwordEncoder;
    private final SellerRepository repository;

    @Autowired
    public SellerSignUpService(
            PasswordEncoder passwordEncoder,
            SellerRepository repository
    ) {
        this.passwordEncoder = passwordEncoder;
        this.repository = repository;
    }

    public void signUp(CreateSellerCommand command) {
        if (!isCommandValid(command)) {
            throw new BusinessException(UserErrorCode.INVALID_COMMAND);
        }

        saveSeller(createSeller(command));
    }

    private void saveSeller(Seller seller) {
        try {
            repository.save(seller);
        } catch (DataIntegrityViolationException exception) {
            throw new BusinessException(UserErrorCode.DUPLICATE_USER_PROPERTY);
        }
    }

    private Seller createSeller(CreateSellerCommand command) {
        var seller = new Seller();
        seller.setId(UUID.randomUUID());
        seller.setEmail(command.email());
        seller.setUsername(command.username());
        seller.setHashedPassword(passwordEncoder.encode(command.password()));
        seller.setContactEmail(command.contactEmail());
        return seller;
    }

    private static boolean isCommandValid(CreateSellerCommand command) {
        return isEmailValid(command.email())
                && isUsernameValid(command.username())
                && isPasswordValid(command.password())
                && isEmailValid(command.contactEmail());
    }
}
