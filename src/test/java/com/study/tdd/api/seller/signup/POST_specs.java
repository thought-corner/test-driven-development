package com.study.tdd.api.seller.signup;

import com.study.tdd.TddApplication;
import com.study.tdd.application.command.CreateSellerCommand;
import com.study.tdd.domain.Seller;
import com.study.tdd.infrastructure.persistence.SellerRepository;
import com.study.tdd.support.TestPasswordEncoderConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static com.study.tdd.support.EmailGenerator.generateEmail;
import static com.study.tdd.support.PasswordGenerator.generatePassword;
import static com.study.tdd.support.UsernameGenerator.generateUsername;

/**
 * {@code POST /seller/signUp} 엔드포인트의 명세(specification)를 검증하는 통합 테스트.
 *
 * <p>구현 세부사항이 아닌 <strong>입출력 계약(HTTP 요청 → 응답)</strong>만을 관찰하는 아웃사이드-인(outside-in) 방식으로 작성되었다.
 * 컨트롤러/서비스/리포지토리를 직접 호출하지 않고, 실제 서버를 띄운 뒤 HTTP로 왕복하여 시스템 전체 동작을 확인한다.</p>
 *
 * <h2>테스트 구성의 핵심</h2>
 * <ul>
 *   <li>{@code webEnvironment = RANDOM_PORT} — 실제 톰캣을 임의 포트로 기동하여 필터·시큐리티·직렬화까지 포함한 진짜 HTTP 경로를 검증한다.</li>
 *   <li>{@link TestPasswordEncoderConfiguration} — 운영용 BCrypt 대신 빠른 인코더를 {@code @Primary}로 주입해 테스트 속도를 확보한다(암호화 <em>결과</em>가 아니라 <em>계약</em>을 검증하므로 알고리즘 교체가 안전하다).</li>
 *   <li>{@code @AutoConfigureTestRestTemplate} — Spring Boot 4.1에서 분리된 {@link TestRestTemplate} 빈을 명시적으로 등록한다.</li>
 * </ul>
 *
 * <p>모든 케이스는 <strong>Arrange-Act-Assert</strong> 3단계로 구조화되어 있으며, 메서드명이 곧 하나의 명세 문장(given-when-then)을 이룬다.</p>
 */
@SpringBootTest(
        classes = {
                TddApplication.class,
                TestPasswordEncoderConfiguration.class
        },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureTestRestTemplate
@DisplayName("POST /seller/signUp")
public class POST_specs {

    /**
     * 회원가입의 <strong>해피 패스(happy path)</strong> — 모든 속성이 유효하면
     * 본문 없이 성공을 뜻하는 {@code 204 No Content}를 반환해야 한다.
     *
     * <p>나머지 케이스들이 검증하는 "실패 계약"의 기준점이 되는 기본 명세이다.</p>
     */
    @Test
    void 올바르게_요청하면_204_No_Content_상태코드를_반환한다(
            @Autowired TestRestTemplate client
    ) {
        // Arrange
        var command = new CreateSellerCommand(
                generateEmail(),
                generateUsername(),
                "password",
                generateEmail()
        );

        // Act
        ResponseEntity<Void> response = client.postForEntity(
                "/seller/signUp",
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
        var command = new CreateSellerCommand(
                null,
                generateUsername(),
                "password",
                generateEmail()
        );

        // Act
        ResponseEntity<Void> response = client.postForEntity(
                "/seller/signUp",
                command,
                Void.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    /**
     * 이메일 <strong>형식</strong> 검증의 대표 명세. 하나의 규칙(정규식)이 막아야 할 여러 경계값을 {@link ValueSource}로 나열해 한 메서드로 검증한다.
     *
     * <p>골뱅이 누락·도메인 누락·TLD 누락 등 "형식은 갖췄지만 무효"인 값들을 모아 검증 로직의 빈틈을 좁힌다. 케이스 추가는 문자열 한 줄로 끝난다.</p>
     */
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
        var command = new CreateSellerCommand(
                email,
                generateUsername(),
                "password",
                generateEmail()
        );

        // Act
        ResponseEntity<Void> response = client.postForEntity(
                "/seller/signUp",
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
        var command = new CreateSellerCommand(
                generateEmail(),
                null,
                "password",
                generateEmail()
        );

        // Act
        ResponseEntity<Void> response = client.postForEntity(
                "/seller/signUp",
                command,
                Void.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "se",
            "seller ",
            "seller.",
            "seller!",
            "seller@"
    })
    void username_속성이_올바른_형식을_따르지_않으면_400_Bad_Request_상태코드를_반환한다(
            String username,
            @Autowired TestRestTemplate client
    ) {
        // Arrange
        var command = new CreateSellerCommand(
                generateEmail(),
                username,
                "password",
                generateEmail()
        );

        // Act
        ResponseEntity<Void> response = client.postForEntity(
                "/seller/signUp",
                command,
                Void.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "seller",
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
            "0123456789",
            "seller_",
            "seller-"
    })
    void username_속성이_올바른_형식을_따르면_204_No_Content_상태코드를_반환한다(
            String username,
            @Autowired TestRestTemplate client
    ) {
        // Arrange
        var command = new CreateSellerCommand(
                generateEmail(),
                username,
                "password",
                generateEmail()
        );

        // Act
        ResponseEntity<Void> response = client.postForEntity(
                "/seller/signUp",
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
        var command = new CreateSellerCommand(
                generateEmail(),
                generateUsername(),
                null,
                generateEmail()
        );

        // Act
        ResponseEntity<Void> response = client.postForEntity(
                "/seller/signUp",
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
        var command = new CreateSellerCommand(
                generateEmail(),
                generateUsername(),
                password,
                generateEmail()
        );

        // Act
        ResponseEntity<Void> response = client.postForEntity(
                "/seller/signUp",
                command,
                Void.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    /**
     * 이메일 <strong>유일성(uniqueness)</strong> 제약 명세.
     *
     * <p>Arrange 단계에서 <em>먼저 한 번 가입</em>시켜 상태를 만든 뒤, 같은 이메일로 재요청하면 실패해야 함을 검증한다.
     * 형식 검증(단일 요청)과 달리 "선행 상태"에 의존하는 계약이라는 점이 핵심이다.
     * 유일성은 DB의 {@code unique} 제약으로 보장되며, 그 위반이 HTTP {@code 400}으로 매핑되는지까지 확인한다.</p>
     */
    @Test
    void email_속성에_이미_존재하는_이메일_주소가_지정되면_400_Bad_Request_상태코드를_반환한다(
            @Autowired TestRestTemplate client
    ) {
        // Arrange
        String email = generateEmail();

        client.postForEntity(
                "/seller/signUp",
                new CreateSellerCommand(
                        email,
                        generateUsername(),
                        "password",
                        generateEmail()
                ),
                Void.class
        );

        // Act
        ResponseEntity<Void> response = client.postForEntity(
                "/seller/signUp",
                new CreateSellerCommand(
                        email,
                        generateUsername(),
                        "password",
                        generateEmail()
                ),
                Void.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @Test
    void username_속성에_이미_존재하는_사용자이름이_지정되면_400_Bad_Request_상태코드를_반환한다(
            @Autowired TestRestTemplate client
    ) {
        // Arrange
        String username = generateUsername();

        client.postForEntity(
                "/seller/signUp",
                new CreateSellerCommand(
                        generateEmail(),
                        username,
                        "password",
                        generateEmail()
                ),
                Void.class
        );

        // Act
        ResponseEntity<Void> response = client.postForEntity(
                "/seller/signUp",
                new CreateSellerCommand(
                        generateEmail(),
                        username,
                        "password",
                        generateEmail()
                ),
                Void.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @Test
    void 비밀번호를_올바르게_암호화한다(
            @Autowired TestRestTemplate client,
            @Autowired SellerRepository repository,
            @Autowired PasswordEncoder encoder
    ) {
        // Arrange
        var command = new CreateSellerCommand(
                generateEmail(),
                generateUsername(),
                generatePassword(),
                generateEmail()
        );

        // Act
        client.postForEntity("/seller/signUp", command, Void.class);

        // Assert
        Seller seller = repository
                .findAll()
                .stream()
                .filter(x -> x.getEmail().equals(command.email()))
                .findFirst()
                .orElseThrow();
        String actual = seller.getHashedPassword();
        assertThat(actual).isNotNull();
        assertThat(encoder.matches(command.password(), actual)).isTrue();
    }

    @ParameterizedTest
    @MethodSource("com.study.tdd.support.TestDataSource#invalidEmails")
    void contactEmail_속성이_올바르게_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
            String contactEmail,
            @Autowired TestRestTemplate client
    ) {
        // Arrange
        var command = new CreateSellerCommand(
                generateEmail(),
                generateUsername(),
                generatePassword(),
                contactEmail
        );

        // Act
        ResponseEntity<Void> response = client.postForEntity(
                "/seller/signUp",
                command,
                Void.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }
}
