package com.study.tdd.infrastructure.jwt;

import java.util.UUID;

import com.study.tdd.application.security.TokenIssuer;
import com.study.tdd.application.security.TokenScope;
import com.study.tdd.infrastructure.config.JwtKeyHolder;

import io.jsonwebtoken.Jwts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenIssuer implements TokenIssuer {

	private final JwtKeyHolder jwtKeyHolder;

	@Autowired
	public JwtTokenIssuer(JwtKeyHolder jwtKeyHolder) {
		this.jwtKeyHolder = jwtKeyHolder;
	}

	@Override
	public String issueToken(UUID subjectId, TokenScope scope) {
		return Jwts
			.builder()
			.subject(subjectId.toString())
			.claim("scp", scope.value())
			.signWith(jwtKeyHolder.key())
			.compact();
	}
}
