package com.study.tdd.support;

import java.util.Base64;

import tools.jackson.databind.ObjectMapper;
import org.assertj.core.api.ThrowingConsumer;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * JWT 형식 검증을 위한 커스텀 단언(assertion).
 *
 * <p>JWT(RFC 7519)는 {@code header.payload.signature} 세 부분이 점({@code .})으로 연결된 형식이다.
 * header와 payload는 Base64URL로 인코딩된 JSON이어야 하고, signature는 Base64URL 문자열이어야 한다.
 * 서명의 유효성까지는 검증하지 않는다 — 그것은 토큰을 소비하는 쪽 명세에서 다룬다.</p>
 */
public class JwtAssertions {

    public static ThrowingConsumer<String> conformsToJwtFormat() {
        return s -> {
            String[] parts = s.split("\\.");
            assertThat(parts).hasSize(3);
            assertThat(parts[0]).matches(JwtAssertions::isBase64UrlEncodedJson);
            assertThat(parts[1]).matches(JwtAssertions::isBase64UrlEncodedJson);
            assertThat(parts[2]).matches(JwtAssertions::isBase64UrlEncoded);
        };
    }

    private static boolean isBase64UrlEncodedJson(String s) {
        try {
            new ObjectMapper().readTree(Base64.getUrlDecoder().decode(s));
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    private static boolean isBase64UrlEncoded(String s) {
        try {
            Base64.getUrlDecoder().decode(s);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }
}
