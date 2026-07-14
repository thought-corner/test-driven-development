package com.study.tdd.api.seller.me;

import com.study.tdd.api.TddApiTest;
import com.study.tdd.api.controller.response.AccessTokenCarrier;
import com.study.tdd.api.controller.response.SellerMeView;
import com.study.tdd.application.command.CreateSellerCommand;
import com.study.tdd.application.query.IssueSellerToken;

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
 * {@code GET /seller/me} 엔드포인트의 명세(specification)를 검증하는 통합 테스트.
 *
 * <p>접근 토큰(access token)으로 인증한 판매자 자신의 정보를 반환하는 계약을 다룬다.
 * 토큰은 회원가입 → 토큰 발급을 거쳐 실제로 발행된 것을 사용하며, HTTP 입출력만 관찰한다.</p>
 *
 * <h2>테스트 시나리오</h2>
 * <ul>
 *   <li>올바르게 요청하면 200 OK 상태코드를 반환한다.</li>
 *   <li>액세스 토큰을 사용하지 않으면 401 Unauthorized 상태코드를 반환한다.</li>
 *   <li>서로 다른 판매자의 식별자는 서로 다르다.</li>
 *   <li>같은 판매자의 식별자는 항상 같아야 한다.</li>
 *   <li>판매자의 기본 정보가 올바르게 설정된다.</li>
 * </ul>
 */
@TddApiTest
@DisplayName("GET /seller/me")
public class GET_specs {

	@Test
	void 올바르게_요청하면_200_OK_상태코드를_반환한다(
		@Autowired TestRestTemplate client
	) {
		// Arrange
		String email = generateEmail();
		String password = generatePassword();

		client.postForEntity(
			"/seller/signUp",
			new CreateSellerCommand(
				email,
				generateUsername(),
				password,
				generateEmail()
			),
			Void.class
		);

		String token = issueToken(client, email, password);

		// Act
		ResponseEntity<SellerMeView> response = client.exchange(
			get("/seller/me")
				.header("Authorization", "Bearer " + token)
				.build(),
			SellerMeView.class
		);

		// Assert
		assertThat(response.getStatusCode().value()).isEqualTo(200);
	}

	@Test
	void 액세스_토큰을_사용하지_않으면_401_Unauthorized_상태코드를_반환한다(
		@Autowired TestRestTemplate client
	) {
		// Act
		ResponseEntity<SellerMeView> response = client.getForEntity(
			"/seller/me",
			SellerMeView.class
		);

		// Assert
		assertThat(response.getStatusCode().value()).isEqualTo(401);
	}

	@Test
	void 서로_다른_판매자의_식별자는_서로_다르다(
		@Autowired TestRestTemplate client
	) {
		// Arrange
		String email1 = generateEmail();
		String password1 = generatePassword();
		client.postForEntity(
			"/seller/signUp",
			new CreateSellerCommand(
				email1,
				generateUsername(),
				password1,
				generateEmail()
			),
			Void.class
		);
		String token1 = issueToken(client, email1, password1);

		String email2 = generateEmail();
		String password2 = generatePassword();
		client.postForEntity(
			"/seller/signUp",
			new CreateSellerCommand(
				email2,
				generateUsername(),
				password2,
				generateEmail()
			),
			Void.class
		);
		String token2 = issueToken(client, email2, password2);

		// Act
		SellerMeView seller1 = getSellerMe(client, token1);
		SellerMeView seller2 = getSellerMe(client, token2);

		// Assert
		assertThat(seller1.id()).isNotEqualTo(seller2.id());
	}

	@Test
	void 같은_판매자의_식별자는_항상_같다(
		@Autowired TestRestTemplate client
	) {
		// Arrange
		String email = generateEmail();
		String password = generatePassword();
		client.postForEntity(
			"/seller/signUp",
			new CreateSellerCommand(
				email,
				generateUsername(),
				password,
				generateEmail()
			),
			Void.class
		);

		String token1 = issueToken(client, email, password);
		String token2 = issueToken(client, email, password);

		// Act
		SellerMeView seller1 = getSellerMe(client, token1);
		SellerMeView seller2 = getSellerMe(client, token2);

		// Assert
		assertThat(seller1.id()).isEqualTo(seller2.id());
	}

	@Test
	void 판매자의_기본_정보가_올바르게_설정된다(
		@Autowired TestRestTemplate client
	) {
		// Arrange
		String email = generateEmail();
		String username = generateUsername();
		String password = generatePassword();
		client.postForEntity(
			"/seller/signUp",
			new CreateSellerCommand(
				email,
				username,
				password,
				generateEmail()
			),
			Void.class
		);

		String token = issueToken(client, email, password);

		// Act
		SellerMeView actual = getSellerMe(client, token);

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
			"/seller/issueToken",
			new IssueSellerToken(email, password),
			AccessTokenCarrier.class
		);
		return requireNonNull(carrier).accessToken();
	}

	private static SellerMeView getSellerMe(TestRestTemplate client, String token) {
		ResponseEntity<SellerMeView> response = client.exchange(
			get("/seller/me")
				.header("Authorization", "Bearer " + token)
				.build(),
			SellerMeView.class
		);
		return requireNonNull(response.getBody());
	}
}
