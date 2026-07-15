package com.study.tdd.api.shopper.issueToken;

import com.study.tdd.api.TddApiTest;
import com.study.tdd.api.controller.response.AccessTokenCarrier;
import com.study.tdd.application.command.CreateShopperCommand;
import com.study.tdd.application.query.IssueShopperToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static com.study.tdd.support.EmailGenerator.generateEmail;
import static com.study.tdd.support.JwtAssertions.conformsToJwtFormat;
import static com.study.tdd.support.PasswordGenerator.generatePassword;
import static com.study.tdd.support.UsernameGenerator.generateUsername;

/**
 * {@code POST /shopper/issueToken} 엔드포인트의 명세(specification)를 검증하는 통합 테스트.
 *
 * <p>구매자 자격증명(이메일 + 비밀번호)을 검증하고 접근 토큰(access token)을 발행하는 계약을 다룬다.
 * 회원가입 명세와 마찬가지로 HTTP 입출력만 관찰하는 아웃사이드-인(outside-in) 방식이다.</p>
 *
 * <h2>테스트 시나리오</h2>
 * <ul>
 *   <li>올바르게 요청하면 200 OK 상태코드를 반환한다.</li>
 *   <li>올바르게 요청하면 액세스 토큰을 반환한다.</li>
 *   <li>액세스 토큰은 JWT 형식을 따른다.</li>
 *   <li>존재하지 않는 이메일 주소가 사용되면 400 Bad Request 상태코드를 반환한다.</li>
 *   <li>잘못된 비밀번호가 사용되면 400 Bad Request 상태코드를 반환한다.</li>
 * </ul>
 *
 * <p>성공 케이스는 Arrange 단계에서 먼저 회원가입을 시켜 "존재하는 구매자"라는 선행 상태를 만든다.
 * 실패 계약은 자격증명이 왜 틀렸는지(이메일 없음/비밀번호 불일치)를 구분하지 않고
 * 동일하게 {@code 400}을 반환한다 — 공격자에게 계정 존재 여부를 노출하지 않기 위함이다.</p>
 */
@TddApiTest
@DisplayName("POST /shopper/issueToken")
public class POST_specs {

    @Test
    void 올바르게_요청하면_200_OK_상태코드를_반환한다(
            @Autowired TestRestTemplate client
    ) {
        // Arrange
        String email = generateEmail();
        String password = generatePassword();

        client.postForEntity(
                "/shopper/signUp",
                new CreateShopperCommand(
                        email,
                        generateUsername(),
                        password
                ),
                Void.class
        );

        // Act
        ResponseEntity<AccessTokenCarrier> response = client.postForEntity(
                "/shopper/issueToken",
                new IssueShopperToken(email, password),
                AccessTokenCarrier.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }

    @Test
    void 올바르게_요청하면_액세스_토큰을_반환한다(
            @Autowired TestRestTemplate client
    ) {
        // Arrange
        String email = generateEmail();
        String password = generatePassword();

        client.postForEntity(
                "/shopper/signUp",
                new CreateShopperCommand(
                        email,
                        generateUsername(),
                        password
                ),
                Void.class
        );

        // Act
        ResponseEntity<AccessTokenCarrier> response = client.postForEntity(
                "/shopper/issueToken",
                new IssueShopperToken(email, password),
                AccessTokenCarrier.class
        );

        // Assert
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().accessToken()).isNotNull();
    }

    @Test
    void 액세스_토큰은_JWT_형식을_따른다(
            @Autowired TestRestTemplate client
    ) {
        // Arrange
        String email = generateEmail();
        String password = generatePassword();

        client.postForEntity(
                "/shopper/signUp",
                new CreateShopperCommand(
                        email,
                        generateUsername(),
                        password
                ),
                Void.class
        );

        // Act
        ResponseEntity<AccessTokenCarrier> response = client.postForEntity(
                "/shopper/issueToken",
                new IssueShopperToken(email, password),
                AccessTokenCarrier.class
        );

        // Assert
        String actual = requireNonNull(response.getBody()).accessToken();
        assertThat(actual).satisfies(conformsToJwtFormat());
    }

    @Test
    void 존재하지_않는_이메일_주소가_사용되면_400_Bad_Request_상태코드를_반환한다(
            @Autowired TestRestTemplate client
    ) {
        // Arrange
        String email = generateEmail();
        String password = generatePassword();

        // Act
        ResponseEntity<AccessTokenCarrier> response = client.postForEntity(
                "/shopper/issueToken",
                new IssueShopperToken(email, password),
                AccessTokenCarrier.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @Test
    void 잘못된_비밀번호가_사용되면_400_Bad_Request_상태코드를_반환한다(
            @Autowired TestRestTemplate client
    ) {
        // Arrange
        String email = generateEmail();
        String password = generatePassword();
        String wrongPassword = generatePassword();

        client.postForEntity(
                "/shopper/signUp",
                new CreateShopperCommand(
                        email,
                        generateUsername(),
                        password
                ),
                Void.class
        );

        // Act
        ResponseEntity<AccessTokenCarrier> response = client.postForEntity(
                "/shopper/issueToken",
                new IssueShopperToken(email, wrongPassword),
                AccessTokenCarrier.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }
}
