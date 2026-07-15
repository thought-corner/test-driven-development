package com.study.tdd.api.shopper.products;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.study.tdd.api.TddApiTest;
import com.study.tdd.api.TestFixture;
import com.study.tdd.api.controller.response.PageCarrier;
import com.study.tdd.api.controller.response.ProductView;
import com.study.tdd.api.controller.response.SellerMeView;
import com.study.tdd.api.controller.response.SellerView;
import com.study.tdd.application.command.RegisterProductCommand;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.RequestEntity.get;
import static com.study.tdd.support.ProductAssertions.isViewDerivedFrom;
import static com.study.tdd.support.RegisterProductCommandGenerator.generateRegisterProductCommand;

/**
 * {@code GET /shopper/products} 엔드포인트의 명세(specification)를 검증하는 통합 테스트.
 *
 * <p>구매자가 상품 목록을 커서 기반 페이지네이션으로 탐색하는 계약을 다룬다.
 * 준비(arrange)는 실제 판매자로 상품을 등록하고 구매자로 전환하는 절차를 {@link TestFixture}로
 * 끌어올려, 각 케이스는 의도만 드러내는 몇 줄로 상황을 구성한다.</p>
 *
 * <h2>테스트 시나리오</h2>
 * <ul>
 *   <li>올바르게 요청하면 200 OK 상태코드를 반환한다.</li>
 *   <li>판매자 접근 토큰을 사용하면 403 Forbidden 상태코드를 반환한다.</li>
 *   <li>첫 번째 페이지의 상품을 반환한다.</li>
 *   <li>상품 목록을 등록 시점 역순으로 정렬한다.</li>
 *   <li>상품 속성을 올바르게 반환한다.</li>
 *   <li>판매자 정보를 올바르게 반환한다.</li>
 *   <li>두 번째 페이지를 올바르게 반환한다.</li>
 *   <li>마지막 페이지를 올바르게 반환한다.</li>
 * </ul>
 */
@TddApiTest
@DisplayName("GET /shopper/products")
public class GET_specs {

	public static final int PAGE_SIZE = 10;

	@Test
	void 올바르게_요청하면_200_OK_상태코드를_반환한다(
		@Autowired TestFixture fixture
	) {
		// Arrange
		fixture.createShopperThenSetAsDefaultUser();

		// Act
		ResponseEntity<PageCarrier<ProductView>> response = fixture.client().exchange(
			get("/shopper/products").build(),
			new ParameterizedTypeReference<>() { }
		);

		// Assert
		assertThat(response.getStatusCode().value()).isEqualTo(200);
	}

	@Test
	void 판매자_접근_토큰을_사용하면_403_Forbidden_상태코드를_반환한다(
		@Autowired TestFixture fixture
	) {
		// Arrange
		fixture.createSellerThenSetAsDefaultUser();

		// Act
		ResponseEntity<PageCarrier<ProductView>> response = fixture.client().exchange(
			get("/shopper/products").build(),
			new ParameterizedTypeReference<>() { }
		);

		// Assert
		assertThat(response.getStatusCode().value()).isEqualTo(403);
	}

	@Test
	void 첫_번째_페이지의_상품을_반환한다(
		@Autowired TestFixture fixture
	) {
		// Arrange
		fixture.deleteAllProducts();

		fixture.createSellerThenSetAsDefaultUser();
		List<UUID> ids = fixture.registerProducts(PAGE_SIZE);

		fixture.createShopperThenSetAsDefaultUser();

		// Act
		ResponseEntity<PageCarrier<ProductView>> response = fixture.client().exchange(
			get("/shopper/products").build(),
			new ParameterizedTypeReference<>() { }
		);

		// Assert
		PageCarrier<ProductView> actual = response.getBody();
		assertThat(actual).isNotNull();
		assertThat(actual.items()).extracting(ProductView::id).containsAll(ids);
	}

	@Test
	void 상품_목록을_등록_시점_역순으로_정렬한다(
		@Autowired TestFixture fixture
	) {
		// Arrange
		fixture.deleteAllProducts();

		fixture.createSellerThenSetAsDefaultUser();
		UUID id1 = fixture.registerProduct();
		UUID id2 = fixture.registerProduct();
		UUID id3 = fixture.registerProduct();

		fixture.createShopperThenSetAsDefaultUser();

		// Act
		ResponseEntity<PageCarrier<ProductView>> response = fixture.client().exchange(
			get("/shopper/products").build(),
			new ParameterizedTypeReference<>() { }
		);

		// Assert
		assertThat(requireNonNull(response.getBody()).items())
			.extracting(ProductView::id)
			.containsExactly(id3, id2, id1);
	}

	@Test
	void 상품_속성을_올바르게_반환한다(
		@Autowired TestFixture fixture
	) {
		// Arrange
		fixture.deleteAllProducts();

		fixture.createSellerThenSetAsDefaultUser();
		RegisterProductCommand command = generateRegisterProductCommand();
		fixture.registerProduct(command);

		fixture.createShopperThenSetAsDefaultUser();

		// Act
		ResponseEntity<PageCarrier<ProductView>> response = fixture.client().exchange(
			get("/shopper/products").build(),
			new ParameterizedTypeReference<>() { }
		);

		// Assert
		ProductView actual = requireNonNull(response.getBody()).items()[0];
		assertThat(actual).satisfies(isViewDerivedFrom(command));
	}

	@Test
	void 판매자_정보를_올바르게_반환한다(
		@Autowired TestFixture fixture
	) {
		// Arrange
		fixture.deleteAllProducts();

		fixture.createSellerThenSetAsDefaultUser();
		SellerMeView seller = fixture.getSeller();
		fixture.registerProduct();

		fixture.createShopperThenSetAsDefaultUser();

		// Act
		ResponseEntity<PageCarrier<ProductView>> response = fixture.client().exchange(
			get("/shopper/products").build(),
			new ParameterizedTypeReference<>() { }
		);

		// Assert
		SellerView actual = requireNonNull(response.getBody()).items()[0].seller();
		assertThat(actual).isNotNull();
		assertThat(actual.id()).isEqualTo(seller.id());
		assertThat(actual.username()).isEqualTo(seller.username());
		assertThat(actual.contactEmail()).isEqualTo(seller.contactEmail());
	}

	@Test
	void 두_번째_페이지를_올바르게_반환한다(
		@Autowired TestFixture fixture
	) {
		// Arrange
		fixture.deleteAllProducts();

		fixture.createSellerThenSetAsDefaultUser();
		fixture.registerProducts(PAGE_SIZE / 2);
		List<UUID> ids = fixture.registerProducts(PAGE_SIZE);
		fixture.registerProducts(PAGE_SIZE);

		fixture.createShopperThenSetAsDefaultUser();
		String token = fixture.consumeProductPage();

		// Act
		ResponseEntity<PageCarrier<ProductView>> response = fixture.client().exchange(
			get("/shopper/products?continuationToken=" + token).build(),
			new ParameterizedTypeReference<>() { }
		);

		// Assert
		assertThat(requireNonNull(response.getBody()).items())
			.extracting(ProductView::id)
			.containsExactlyElementsOf(reversed(ids));
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, PAGE_SIZE })
	void 마지막_페이지를_올바르게_반환한다(
		int lastPageSize,
		@Autowired TestFixture fixture
	) {
		// Arrange
		fixture.deleteAllProducts();

		fixture.createSellerThenSetAsDefaultUser();
		List<UUID> ids = fixture.registerProducts(lastPageSize);
		fixture.registerProducts(PAGE_SIZE * 2);

		fixture.createShopperThenSetAsDefaultUser();
		String token = fixture.consumeTwoProductPages();

		// Act
		ResponseEntity<PageCarrier<ProductView>> response = fixture.client().exchange(
			get("/shopper/products?continuationToken=" + token).build(),
			new ParameterizedTypeReference<>() { }
		);

		// Assert
		PageCarrier<ProductView> actual = response.getBody();
		assertThat(requireNonNull(actual).items())
			.extracting(ProductView::id)
			.containsExactlyElementsOf(reversed(ids));
		assertThat(actual.continuationToken()).isNull();
	}

	private static List<UUID> reversed(List<UUID> ids) {
		List<UUID> copy = new ArrayList<>(ids);
		Collections.reverse(copy);
		return copy;
	}
}
