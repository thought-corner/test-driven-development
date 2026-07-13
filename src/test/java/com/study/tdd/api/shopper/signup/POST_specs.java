package com.study.tdd.api.shopper.signup;

import com.study.tdd.api.TddApiTest;
import com.study.tdd.application.command.CreateShopperCommand;
import com.study.tdd.domain.Shopper;
import com.study.tdd.infrastructure.persistence.ShopperRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static com.study.tdd.support.EmailGenerator.generateEmail;
import static com.study.tdd.support.PasswordGenerator.generatePassword;
import static com.study.tdd.support.UsernameGenerator.generateUsername;

/**
 * {@code POST /shopper/signUp} 엔드포인트의 명세(specification)를 검증하는 통합 테스트.
 *
 * <p>구매자(Shopper) 회원가입 계약을 다룬다. 판매자 회원가입과 정책이 같지만
 * 연락 이메일(contactEmail) 속성이 없다는 점이 다르다.
 * 입출력 계약(HTTP 요청 → 응답)만을 관찰하는 아웃사이드-인(outside-in) 방식으로 작성되었다.</p>
 *
 * <p>테스트 부트스트랩 구성(실서버 기동, 테스트용 인코더, {@link TestRestTemplate} 등록)은
 * {@link TddApiTest}에 합성되어 있다.</p>
 *
 * <p>모든 케이스는 <strong>Arrange-Act-Assert</strong> 3단계로 구조화되어 있으며, 메서드명이 곧 하나의 명세 문장(given-when-then)을 이룬다.</p>
 */
@TddApiTest
@DisplayName("POST /shopper/signUp")
public class POST_specs {

    /**
     * 회원가입의 <strong>해피 패스(happy path)</strong> — 모든 속성이 유효하면
     * 본문 없이 성공을 뜻하는 {@code 204 No Content}를 반환해야 한다.
     */
    @Test
    void 올바르게_요청하면_204_No_Content_상태코드를_반환한다(
            @Autowired TestRestTemplate client
    ) {
        // Arrange
        var command = new CreateShopperCommand(
                generateEmail(),
                generateUsername(),
                generatePassword()
        );

        // Act
        ResponseEntity<Void> response = client.postForEntity(
                "/shopper/signUp",
                command,
                Void.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(204);
    }

    @Test
    void email_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
            @Autowired TestRestTemplate client
    ) {
        // Arrange
        var command = new CreateShopperCommand(
                null,
                generateUsername(),
                generatePassword()
        );

        // Act
        ResponseEntity<Void> response = client.postForEntity(
                "/shopper/signUp",
                command,
                Void.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "invalid-email",
            "invalid-email@",
            "invalid-email@test",
            "invalid-email@test.",
            "invalid-email@.com"
    })
    void email_속성이_올바른_형식을_따르지_않으면_400_Bad_Request_상태코드를_반환한다(
            String email,
            @Autowired TestRestTemplate client
    ) {
        // Arrange
        var command = new CreateShopperCommand(
                email,
                generateUsername(),
                generatePassword()
        );

        // Act
        ResponseEntity<Void> response = client.postForEntity(
                "/shopper/signUp",
                command,
                Void.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @Test
    void username_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
            @Autowired TestRestTemplate client
    ) {
        // Arrange
        var command = new CreateShopperCommand(
                generateEmail(),
                null,
                generatePassword()
        );

        // Act
        ResponseEntity<Void> response = client.postForEntity(
                "/shopper/signUp",
                command,
                Void.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "sh",
            "shopper ",
            "shopper.",
            "shopper!",
            "shopper@"
    })
    void username_속성이_올바른_형식을_따르지_않으면_400_Bad_Request_상태코드를_반환한다(
            String username,
            @Autowired TestRestTemplate client
    ) {
        // Arrange
        var command = new CreateShopperCommand(
                generateEmail(),
                username,
                generatePassword()
        );

        // Act
        ResponseEntity<Void> response = client.postForEntity(
                "/shopper/signUp",
                command,
                Void.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "shopper",
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
            "0123456789",
            "shopper_",
            "shopper-"
    })
    void username_속성이_올바른_형식을_따르면_204_No_Content_상태코드를_반환한다(
            String username,
            @Autowired TestRestTemplate client
    ) {
        // Arrange
        var command = new CreateShopperCommand(
                generateEmail(),
                username,
                generatePassword()
        );

        // Act
        ResponseEntity<Void> response = client.postForEntity(
                "/shopper/signUp",
                command,
                Void.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(204);
    }

    @Test
    void password_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
            @Autowired TestRestTemplate client
    ) {
        // Arrange
        var command = new CreateShopperCommand(
                generateEmail(),
                generateUsername(),
                null
        );

        // Act
        ResponseEntity<Void> response = client.postForEntity(
                "/shopper/signUp",
                command,
                Void.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @ParameterizedTest
    @MethodSource("com.study.tdd.support.TestDataSource#invalidPasswords")
    void password_속성이_올바른_형식을_따르지_않으면_400_Bad_Request_상태코드를_반환한다(
            String password,
            @Autowired TestRestTemplate client
    ) {
        // Arrange
        var command = new CreateShopperCommand(
                generateEmail(),
                generateUsername(),
                password
        );

        // Act
        ResponseEntity<Void> response = client.postForEntity(
                "/shopper/signUp",
                command,
                Void.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    /**
     * 이메일 <strong>유일성(uniqueness)</strong> 제약 명세.
     *
     * <p>Arrange 단계에서 먼저 한 번 가입시켜 상태를 만든 뒤,
     * 같은 이메일로 재요청하면 실패해야 함을 검증한다.</p>
     */
    @Test
    void email_속성에_이미_존재하는_이메일_주소가_지정되면_400_Bad_Request_상태코드를_반환한다(
            @Autowired TestRestTemplate client
    ) {
        // Arrange
        String email = generateEmail();

        client.postForEntity(
                "/shopper/signUp",
                new CreateShopperCommand(
                        email,
                        generateUsername(),
                        generatePassword()
                ),
                Void.class
        );

        // Act
        ResponseEntity<Void> response = client.postForEntity(
                "/shopper/signUp",
                new CreateShopperCommand(
                        email,
                        generateUsername(),
                        generatePassword()
                ),
                Void.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @Test
    void username_속성에_이미_존재하는_사용자_이름이_지정되면_400_Bad_Request_상태코드를_반환한다(
            @Autowired TestRestTemplate client
    ) {
        // Arrange
        String username = generateUsername();

        client.postForEntity(
                "/shopper/signUp",
                new CreateShopperCommand(
                        generateEmail(),
                        username,
                        generatePassword()
                ),
                Void.class
        );

        // Act
        ResponseEntity<Void> response = client.postForEntity(
                "/shopper/signUp",
                new CreateShopperCommand(
                        generateEmail(),
                        username,
                        generatePassword()
                ),
                Void.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @Test
    void 비밀번호를_올바르게_암호화한다(
            @Autowired TestRestTemplate client,
            @Autowired ShopperRepository repository,
            @Autowired PasswordEncoder encoder
    ) {
        // Arrange
        var command = new CreateShopperCommand(
                generateEmail(),
                generateUsername(),
                generatePassword()
        );

        // Act
        client.postForEntity("/shopper/signUp", command, Void.class);

        // Assert
        Shopper shopper = repository
                .findAll()
                .stream()
                .filter(x -> x.getEmail().equals(command.email()))
                .findFirst()
                .orElseThrow();
        String actual = shopper.getHashedPassword();
        assertThat(actual).isNotNull();
        assertThat(encoder.matches(command.password(), actual)).isTrue();
    }
}
