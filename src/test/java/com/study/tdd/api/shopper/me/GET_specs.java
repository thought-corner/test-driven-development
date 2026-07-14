package com.study.tdd.api.shopper.me;

import com.study.tdd.api.TddApiTest;
import com.study.tdd.api.controller.response.AccessTokenCarrier;
import com.study.tdd.api.controller.response.ShopperMeView;
import com.study.tdd.application.command.CreateShopperCommand;
import com.study.tdd.application.query.IssueShopperToken;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.RequestEntity.get;
import static com.study.tdd.support.EmailGenerator.generateEmail;
import static com.study.tdd.support.PasswordGenerator.generatePassword;
import static com.study.tdd.support.UsernameGenerator.generateUsername;

/**
 * {@code GET /shopper/me} 엔드포인트의 명세(specification)를 검증하는 통합 테스트.
 *
 * <p>접근 토큰(access token)으로 인증한 구매자 자신의 정보를 반환하는 계약을 다룬다.
 * 토큰은 회원가입 → 토큰 발급을 거쳐 실제로 발행된 것을 사용하며, HTTP 입출력만 관찰한다.
 * 판매자와 정책이 같지만 연락 이메일(contactEmail) 속성이 없다는 점이 다르다.</p>
 *
 * <h2>테스트 시나리오</h2>
 * <ul>
 *   <li>올바르게 요청하면 200 OK 상태코드를 반환한다.</li>
 *   <li>액세스 토큰을 사용하지 않으면 401 Unauthorized 상태코드를 반환한다.</li>
 *   <li>서로 다른 구매자의 식별자는 서로 다르다.</li>
 *   <li>같은 구매자의 식별자는 항상 같아야 한다.</li>
 *   <li>구매자의 기본 정보가 올바르게 설정된다.</li>
 * </ul>
 */
@TddApiTest
@DisplayName("GET /shopper/me")
public class GET_specs {

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

		String token = issueToken(client, email, password);

		// Act
		ResponseEntity<ShopperMeView> response = client.exchange(
			get("/shopper/me")
				.header("Authorization", "Bearer " + token)
				.build(),
			ShopperMeView.class
		);

		// Assert
		assertThat(response.getStatusCode().value()).isEqualTo(200);
	}

	@Test
	void 액세스_토큰을_사용하지_않으면_401_Unauthorized_상태코드를_반환한다(
		@Autowired TestRestTemplate client
	) {
		// Act
		ResponseEntity<ShopperMeView> response = client.getForEntity(
			"/shopper/me",
			ShopperMeView.class
		);

		// Assert
		assertThat(response.getStatusCode().value()).isEqualTo(401);
	}

	@Test
	void 서로_다른_구매자의_식별자는_서로_다르다(
		@Autowired TestRestTemplate client
	) {
		// Arrange
		String email1 = generateEmail();
		String password1 = generatePassword();
		client.postForEntity(
			"/shopper/signUp",
			new CreateShopperCommand(
				email1,
				generateUsername(),
				password1
			),
			Void.class
		);
		String token1 = issueToken(client, email1, password1);

		String email2 = generateEmail();
		String password2 = generatePassword();
		client.postForEntity(
			"/shopper/signUp",
			new CreateShopperCommand(
				email2,
				generateUsername(),
				password2
			),
			Void.class
		);
		String token2 = issueToken(client, email2, password2);

		// Act
		ShopperMeView shopper1 = getShopperMe(client, token1);
		ShopperMeView shopper2 = getShopperMe(client, token2);

		// Assert
		assertThat(shopper1.id()).isNotEqualTo(shopper2.id());
	}

	@Test
	void 같은_구매자의_식별자는_항상_같다(
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

		String token1 = issueToken(client, email, password);
		String token2 = issueToken(client, email, password);

		// Act
		ShopperMeView shopper1 = getShopperMe(client, token1);
		ShopperMeView shopper2 = getShopperMe(client, token2);

		// Assert
		assertThat(shopper1.id()).isEqualTo(shopper2.id());
	}

	@Test
	void 구매자의_기본_정보가_올바르게_설정된다(
		@Autowired TestRestTemplate client
	) {
		// Arrange
		String email = generateEmail();
		String username = generateUsername();
		String password = generatePassword();
		client.postForEntity(
			"/shopper/signUp",
			new CreateShopperCommand(
				email,
				username,
				password
			),
			Void.class
		);

		String token = issueToken(client, email, password);

		// Act
		ShopperMeView actual = getShopperMe(client, token);

		// Assert
		assertThat(actual.email()).isEqualTo(email);
		assertThat(actual.username()).isEqualTo(username);
	}

	private static String issueToken(
		TestRestTemplate client,
		String email,
		String password
	) {
		AccessTokenCarrier carrier = client.postForObject(
			"/shopper/issueToken",
			new IssueShopperToken(email, password),
			AccessTokenCarrier.class
		);
		return requireNonNull(carrier).accessToken();
	}

	private static ShopperMeView getShopperMe(TestRestTemplate client, String token) {
		ResponseEntity<ShopperMeView> response = client.exchange(
			get("/shopper/me")
				.header("Authorization", "Bearer " + token)
				.build(),
			ShopperMeView.class
		);
		return requireNonNull(response.getBody());
	}
}
