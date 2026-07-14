package com.study.tdd.api.seller.products;

import java.net.URI;
import java.util.UUID;
import java.util.function.Predicate;

import com.study.tdd.api.TddApiTest;
import com.study.tdd.api.TestFixture;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static com.study.tdd.support.RegisterProductCommandGenerator.generateRegisterProductCommand;
import static com.study.tdd.support.RegisterProductCommandGenerator.generateRegisterProductCommandWithImageUri;

/**
 * {@code POST /seller/products} 엔드포인트의 명세(specification)를 검증하는 통합 테스트.
 *
 * <p>인증된 판매자가 상품을 등록하는 계약을 다룬다. 등록에 성공하면 201 Created와 함께
 * 등록된 상품 자원을 가리키는 {@code Location} 헤더를 반환한다.</p>
 *
 * <h2>테스트 시나리오</h2>
 * <ul>
 *   <li>올바르게 요청하면 201 Created 상태코드를 반환한다.</li>
 *   <li>판매자가 아닌 사용자의 접근 토큰을 사용하면 403 Forbidden 상태코드를 반환한다.</li>
 *   <li>imageUri 속성이 URI 형식을 따르지 않으면 400 Bad Request 상태코드를 반환한다.</li>
 *   <li>올바르게 요청하면 등록된 상품 정보에 접근하는 Location 헤더를 반환한다.</li>
 * </ul>
 */
@TddApiTest
@DisplayName("POST /seller/products")
public class POST_specs {

	@Test
	void 올바르게_요청하면_201_Created_상태코드를_반환한다(
		@Autowired TestFixture fixture
	) {
		// Arrange
		fixture.createSellerThenSetAsDefaultUser();

		// Act
		ResponseEntity<Void> response = fixture.client().postForEntity(
			"/seller/products",
			generateRegisterProductCommand(),
			Void.class
		);

		// Assert
		assertThat(response.getStatusCode().value()).isEqualTo(201);
	}

	@Test
	void 판매자가_아닌_사용자의_접근_토큰을_사용하면_403_Forbidden_상태코드를_반환한다(
		@Autowired TestFixture fixture
	) {
		// Arrange
		fixture.createShopperThenSetAsDefaultUser();

		// Act
		ResponseEntity<Void> response = fixture.client().postForEntity(
			"/seller/products",
			generateRegisterProductCommand(),
			Void.class
		);

		// Assert
		assertThat(response.getStatusCode().value()).isEqualTo(403);
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"invalid-uri",
		"http://",
		"://missing.scheme.com"
	})
	void imageUri_속성이_URI_형식을_따르지_않으면_400_Bad_Request_상태코드를_반환한다(
		String imageUri,
		@Autowired TestFixture fixture
	) {
		// Arrange
		fixture.createSellerThenSetAsDefaultUser();

		// Act
		ResponseEntity<Void> response = fixture.client().postForEntity(
			"/seller/products",
			generateRegisterProductCommandWithImageUri(imageUri),
			Void.class
		);

		// Assert
		assertThat(response.getStatusCode().value()).isEqualTo(400);
	}

	@Test
	void 올바르게_요청하면_등록된_상품_정보에_접근하는_Location_헤더를_반환한다(
		@Autowired TestFixture fixture
	) {
		// Arrange
		fixture.createSellerThenSetAsDefaultUser();

		// Act
		ResponseEntity<Void> response = fixture.client().postForEntity(
			"/seller/products",
			generateRegisterProductCommand(),
			Void.class
		);

		// Assert
		URI actual = response.getHeaders().getLocation();
		assertThat(actual).isNotNull();
		assertThat(actual.isAbsolute()).isFalse();
		assertThat(actual.getPath())
			.startsWith("/seller/products/")
			.matches(endsWithUUID());
	}

	private Predicate<? super String> endsWithUUID() {
		return path -> {
			String[] segments = path.split("/");
			String lastSegment = segments[segments.length - 1];
			try {
				UUID.fromString(lastSegment);
				return true;
			} catch (IllegalArgumentException exception) {
				return false;
			}
		};
	}
}
