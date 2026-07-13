package com.study.tdd.application;

import java.util.Optional;

import com.study.tdd.application.query.IssueSellerToken;
import com.study.tdd.infrastructure.jwt.JwtComposer;
import com.study.tdd.infrastructure.persistence.SellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SellerIssueTokenService {

    private final PasswordEncoder passwordEncoder;
    private final SellerRepository repository;
    private final JwtComposer jwtComposer;

    @Autowired
    public SellerIssueTokenService(
            PasswordEncoder passwordEncoder,
            SellerRepository repository,
            JwtComposer jwtComposer
    ) {
        this.passwordEncoder = passwordEncoder;
        this.repository = repository;
        this.jwtComposer = jwtComposer;
    }

    public Optional<String> issueToken(IssueSellerToken query) {
        return repository
                .findByEmail(query.email())
                .filter(seller -> passwordEncoder.matches(
                        query.password(),
                        seller.getHashedPassword()
                ))
                .map(seller -> jwtComposer.composeToken(seller.getId(), "seller"));
    }
}
