package com.study.tdd.application;

import java.util.Optional;

import com.study.tdd.application.query.IssueShopperToken;
import com.study.tdd.application.security.TokenIssuer;
import com.study.tdd.application.security.TokenScope;
import com.study.tdd.infrastructure.persistence.ShopperRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ShopperIssueTokenService {

	private final PasswordEncoder passwordEncoder;
	private final ShopperRepository repository;
	private final TokenIssuer tokenIssuer;

	@Autowired
	public ShopperIssueTokenService(
		PasswordEncoder passwordEncoder,
		ShopperRepository repository,
		TokenIssuer tokenIssuer
	) {
		this.passwordEncoder = passwordEncoder;
		this.repository = repository;
		this.tokenIssuer = tokenIssuer;
	}

	public Optional<String> issueToken(IssueShopperToken query) {
		return repository
			.findByEmail(query.email())
			.filter(shopper -> passwordEncoder.matches(
				query.password(),
				shopper.getHashedPassword()
			))
			.map(shopper -> tokenIssuer.issueToken(shopper.getId(), TokenScope.SHOPPER));
	}
}
