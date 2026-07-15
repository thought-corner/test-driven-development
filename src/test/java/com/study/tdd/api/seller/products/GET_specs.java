package com.study.tdd.api.seller.products;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.study.tdd.api.TddApiTest;
import com.study.tdd.api.TestFixture;
import com.study.tdd.api.controller.response.ArrayCarrier;
import com.study.tdd.api.controller.response.SellerProductView;
import com.study.tdd.application.command.RegisterProductCommand;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;

import static java.time.ZoneOffset.UTC;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Comparator.reverseOrder;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.springframework.http.RequestEntity.get;
import static com.study.tdd.support.ProductAssertions.isDerivedFrom;
import static com.study.tdd.support.RegisterProductCommandGenerator.generateRegisterProductCommand;

/**
 * {@code GET /seller/products} 엔드포인트의 명세(specification)를 검증하는 통합 테스트.
 *
 * <p>인증된 판매자가 자신이 등록한 상품 목록을 조회하는 계약을 다룬다. 다른 판매자의 상품은
 * 포함되지 않으며, 목록은 등록 시각을 기준으로 최신순(역순)으로 정렬된다.</p>
 *
 * <h2>테스트 시나리오</h2>
 * <ul>
 *   <li>올바르게 요청하면 200 OK 상태코드를 반환한다.</li>
 *   <li>판매자가 등록한 모든 상품을 반환한다.</li>
 *   <li>다른 판매자가 등록한 상품이 포함되지 않는다.</li>
 *   <li>상품 정보를 올바르게 반환한다.</li>
 *   <li>상품 등록 시각을 올바르게 반환한다.</li>
 *   <li>상품 목록을 등록 시점 역순으로 정렬한다.</li>
 * </ul>
 */
@TddApiTest
@DisplayName("GET /seller/products")
public class GET_specs {

	@Test
	void 올바르게_요청하면_200_OK_상태코드를_반환한다(
		@Autowired TestFixture fixture
	) {
		// Arrange
		fixture.createSellerThenSetAsDefaultUser();

		// Act
		ResponseEntity<ArrayCarrier<SellerProductView>> response =
			fixture.client().exchange(
				get("/seller/products").build(),
				new ParameterizedTypeReference<>() { }
			);

		// Assert
		assertThat(response.getStatusCode().value()).isEqualTo(200);
	}

	@Test
	void 판매자가_등록한_모든_상품을_반환한다(
		@Autowired TestFixture fixture
	) {
		// Arrange
		fixture.createSellerThenSetAsDefaultUser();
		List<UUID> ids = fixture.registerProducts();

		// Act
		ResponseEntity<ArrayCarrier<SellerProductView>> response =
			fixture.client().exchange(
				get("/seller/products").build(),
				new ParameterizedTypeReference<>() { }
			);

		// Assert
		ArrayCarrier<SellerProductView> actual = response.getBody();
		assertThat(actual).isNotNull();
		assertThat(actual.items())
			.extracting(SellerProductView::id)
			.containsAll(ids);
	}

	@Test
	void 다른_판매자가_등록한_상품이_포함되지_않는다(
		@Autowired TestFixture fixture
	) {
		// Arrange
		fixture.createSellerThenSetAsDefaultUser();
		UUID unexpected = fixture.registerProduct();

		fixture.createSellerThenSetAsDefaultUser();
		fixture.registerProducts();

		// Act
		ResponseEntity<ArrayCarrier<SellerProductView>> response =
			fixture.client().exchange(
				get("/seller/products").build(),
				new ParameterizedTypeReference<>() { }
			);

		// Assert
		assertThat(requireNonNull(response.getBody()).items())
			.extracting(SellerProductView::id)
			.doesNotContain(unexpected);
	}

	@Test
	void 상품_정보를_올바르게_반환한다(
		@Autowired TestFixture fixture
	) {
		// Arrange
		fixture.createSellerThenSetAsDefaultUser();
		RegisterProductCommand command = generateRegisterProductCommand();
		fixture.registerProduct(command);

		// Act
		ResponseEntity<ArrayCarrier<SellerProductView>> response =
			fixture.client().exchange(
				get("/seller/products").build(),
				new ParameterizedTypeReference<>() { }
			);

		// Assert
		SellerProductView actual = requireNonNull(response.getBody()).items()[0];
		assertThat(actual).satisfies(isDerivedFrom(command));
	}

	@Test
	void 상품_등록_시각을_올바르게_반환한다(
		@Autowired TestFixture fixture
	) {
		// Arrange
		fixture.createSellerThenSetAsDefaultUser();
		LocalDateTime referenceTime = LocalDateTime.now(UTC);
		fixture.registerProduct();

		// Act
		ResponseEntity<ArrayCarrier<SellerProductView>> response =
			fixture.client().exchange(
				get("/seller/products").build(),
				new ParameterizedTypeReference<>() { }
			);

		// Assert
		SellerProductView actual = requireNonNull(response.getBody()).items()[0];
		assertThat(actual.registeredTimeUtc())
			.isCloseTo(referenceTime, within(1, SECONDS));
	}

	@Test
	void 상품_목록을_등록_시점_역순으로_정렬한다(
		@Autowired TestFixture fixture
	) {
		// Arrange
		fixture.createSellerThenSetAsDefaultUser();
		fixture.registerProducts();

		// Act
		ResponseEntity<ArrayCarrier<SellerProductView>> response =
			fixture.client().exchange(
				get("/seller/products").build(),
				new ParameterizedTypeReference<>() { }
			);

		// Assert
		assertThat(requireNonNull(response.getBody()).items())
			.extracting(SellerProductView::registeredTimeUtc)
			.isSortedAccordingTo(reverseOrder());
	}
}
