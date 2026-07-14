package com.study.tdd.infrastructure.jwt;

import java.util.UUID;

import com.study.tdd.infrastructure.config.JwtKeyHolder;

import io.jsonwebtoken.Jwts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JwtComposer {

	private final JwtKeyHolder jwtKeyHolder;

	@Autowired
	public JwtComposer(JwtKeyHolder jwtKeyHolder) {
		this.jwtKeyHolder = jwtKeyHolder;
	}

	public String composeToken(UUID userId, String scope) {
		return Jwts
			.builder()
			.subject(userId.toString())
			.claim("scp", scope)
			.signWith(jwtKeyHolder.key())
			.compact();
	}
}
