package com.study.tdd.api.seller.products.id;

import java.time.LocalDateTime;
import java.util.UUID;

import com.study.tdd.api.TddApiTest;
import com.study.tdd.api.TestFixture;
import com.study.tdd.api.controller.response.SellerProductView;
import com.study.tdd.application.command.RegisterProductCommand;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import static java.time.ZoneOffset.UTC;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static com.study.tdd.support.ProductAssertions.isDerivedFrom;
import static com.study.tdd.support.RegisterProductCommandGenerator.generateRegisterProductCommand;

/**
 * {@code GET /seller/products/{id}} 엔드포인트의 명세(specification)를 검증하는 통합 테스트.
 *
 * <p>인증된 판매자가 자신이 등록한 특정 상품을 조회하는 계약을 다룬다. 다른 판매자의 상품이나
 * 존재하지 않는 식별자는 404 Not Found로 응답한다.</p>
 *
 * <h2>테스트 시나리오</h2>
 * <ul>
 *   <li>올바르게 요청하면 200 OK 상태코드를 반환한다.</li>
 *   <li>판매자가 아닌 사용자의 접근 토큰을 사용하면 403 Forbidden 상태코드를 반환한다.</li>
 *   <li>존재하지 않는 식별자를 사용하면 404 Not Found 상태코드를 반환한다.</li>
 *   <li>다른 판매자가 등록한 상품 식별자를 사용하면 404 Not Found 상태코드를 반환한다.</li>
 *   <li>상품 식별자를 올바르게 반환한다.</li>
 *   <li>상품 정보를 올바르게 반환한다.</li>
 *   <li>상품 등록 시각을 올바르게 반환한다.</li>
 * </ul>
 */
@TddApiTest
@DisplayName("GET /seller/products/{id}")
public class GET_specs {

	@Test
	void 올바르게_요청하면_200_OK_상태코드를_반환한다(
		@Autowired TestFixture fixture
	) {
		// Arrange
		fixture.createSellerThenSetAsDefaultUser();
		UUID id = fixture.registerProduct();

		// Act
		ResponseEntity<SellerProductView> response = fixture.client().getForEntity(
			"/seller/products/" + id,
			SellerProductView.class
		);

		// Assert
		assertThat(response.getStatusCode().value()).isEqualTo(200);
	}

	@Test
	void 판매자가_아닌_사용자의_접근_토큰을_사용하면_403_Forbidden_상태코드를_반환한다(
		@Autowired TestFixture fixture
	) {
		// Arrange
		fixture.createSellerThenSetAsDefaultUser();
		UUID id = fixture.registerProduct();

		fixture.createShopperThenSetAsDefaultUser();

		// Act
		ResponseEntity<SellerProductView> response = fixture.client().getForEntity(
			"/seller/products/" + id,
			SellerProductView.class
		);

		// Assert
		assertThat(response.getStatusCode().value()).isEqualTo(403);
	}

	@Test
	void 존재하지_않는_식별자를_사용하면_404_Not_Found_상태코드를_반환한다(
		@Autowired TestFixture fixture
	) {
		// Arrange
		fixture.createSellerThenSetAsDefaultUser();
		UUID id = UUID.randomUUID();

		// Act
		ResponseEntity<SellerProductView> response = fixture.client().getForEntity(
			"/seller/products/" + id,
			SellerProductView.class
		);

		// Assert
		assertThat(response.getStatusCode().value()).isEqualTo(404);
	}

	@Test
	void 다른_판매자가_등록한_상품_식별자를_사용하면_404_Not_Found_상태코드를_반환한다(
		@Autowired TestFixture fixture
	) {
		// Arrange
		fixture.createSellerThenSetAsDefaultUser();
		UUID id = fixture.registerProduct();

		fixture.createSellerThenSetAsDefaultUser();

		// Act
		ResponseEntity<SellerProductView> response = fixture.client().getForEntity(
			"/seller/products/" + id,
			SellerProductView.class
		);

		// Assert
		assertThat(response.getStatusCode().value()).isEqualTo(404);
	}

	@Test
	void 상품_식별자를_올바르게_반환한다(
		@Autowired TestFixture fixture
	) {
		// Arrange
		fixture.createSellerThenSetAsDefaultUser();
		UUID id = fixture.registerProduct();

		// Act
		SellerProductView actual = fixture.client().getForObject(
			"/seller/products/" + id,
			SellerProductView.class
		);

		// Assert
		assertThat(actual).isNotNull();
		assertThat(actual.id()).isEqualTo(id);
	}

	@Test
	void 상품_정보를_올바르게_반환한다(
		@Autowired TestFixture fixture
	) {
		// Arrange
		fixture.createSellerThenSetAsDefaultUser();
		RegisterProductCommand command = generateRegisterProductCommand();
		UUID id = fixture.registerProduct(command);

		// Act
		SellerProductView actual = fixture.client().getForObject(
			"/seller/products/" + id,
			SellerProductView.class
		);

		// Assert
		assertThat(actual).satisfies(isDerivedFrom(command));
	}

	@Test
	void 상품_등록_시각을_올바르게_반환한다(
		@Autowired TestFixture fixture
	) {
		// Arrange
		fixture.createSellerThenSetAsDefaultUser();
		LocalDateTime referenceTime = LocalDateTime.now(UTC);
		UUID id = fixture.registerProduct();

		// Act
		SellerProductView actual = fixture.client().getForObject(
			"/seller/products/" + id,
			SellerProductView.class
		);

		// Assert
		assertThat(actual.registeredTimeUtc())
			.isCloseTo(referenceTime, within(1, SECONDS));
	}
}
