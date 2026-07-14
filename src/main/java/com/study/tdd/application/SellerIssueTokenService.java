package com.study.tdd.application;

import java.util.Optional;

import com.study.tdd.application.query.IssueSellerToken;
import com.study.tdd.application.security.TokenIssuer;
import com.study.tdd.application.security.TokenScope;
import com.study.tdd.infrastructure.persistence.SellerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SellerIssueTokenService {

	private final PasswordEncoder passwordEncoder;
	private final SellerRepository repository;
	private final TokenIssuer tokenIssuer;

	@Autowired
	public SellerIssueTokenService(
		PasswordEncoder passwordEncoder,
		SellerRepository repository,
		TokenIssuer tokenIssuer
	) {
		this.passwordEncoder = passwordEncoder;
		this.repository = repository;
		this.tokenIssuer = tokenIssuer;
	}

	public Optional<String> issueToken(IssueSellerToken query) {
		return repository
			.findByEmail(query.email())
			.filter(seller -> passwordEncoder.matches(
				query.password(),
				seller.getHashedPassword()
			))
			.map(seller -> tokenIssuer.issueToken(seller.getId(), TokenScope.SELLER));
	}
}
