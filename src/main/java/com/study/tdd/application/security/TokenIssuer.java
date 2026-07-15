package com.study.tdd.application.security;

import java.util.UUID;

public interface TokenIssuer {

	String issueToken(UUID subjectId, TokenScope scope);
}
