package com.study.tdd.infrastructure.config;

import javax.crypto.SecretKey;

public record JwtKeyHolder(SecretKey key) {
}
