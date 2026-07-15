package com.study.tdd.application;

import java.util.List;

import com.study.tdd.api.controller.response.PageCarrier;
import com.study.tdd.api.controller.response.ProductView;
import com.study.tdd.application.query.ContinuationToken;
import com.study.tdd.application.query.GetProductPage;
import com.study.tdd.application.query.ProductSellerTuple;
import com.study.tdd.infrastructure.persistence.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 * 구매자에게 상품 목록의 한 페이지를 조립해 주는 애플리케이션 서비스.
 *
 * <p>각 책임을 협력자에게 위임하고, 이 서비스는 <strong>"한 페이지를 조립한다"</strong>는
 * 하나의 책임만 갖는다(SRP).</p>
 *
 * <ul>
 *   <li>토큰 ↔ 커서 변환 → {@link ContinuationToken}</li>
 *   <li>커서 기반 페이지 조회 → {@link ProductRepository#findProductPage}</li>
 *   <li>조회 결과(투영) 보관 → {@link ProductSellerTuple}</li>
 *   <li>투영 → 뷰 변환 → {@link ProductView#from}</li>
 * </ul>
 *
 * <p>다음 페이지 존재 여부를 알기 위해 한 건 더 조회한 뒤({@code PAGE_SIZE + 1}),
 * 초과분의 커서를 다음 이어보기 토큰으로 삼는다.</p>
 */
@Service
public class ProductCatalogService {

	private static final int PAGE_SIZE = 10;

	private final ProductRepository repository;

	@Autowired
	public ProductCatalogService(ProductRepository repository) {
		this.repository = repository;
	}

	public PageCarrier<ProductView> getProductPage(GetProductPage query) {
		Long cursor = ContinuationToken.decode(query.continuationToken());

		List<ProductSellerTuple> results = repository.findProductPage(
			cursor,
			PageRequest.of(0, PAGE_SIZE + 1)
		);

		ProductView[] items = results.stream()
			.limit(PAGE_SIZE)
			.map(tuple -> ProductView.from(tuple.product(), tuple.seller()))
			.toArray(ProductView[]::new);

		return new PageCarrier<>(items, ContinuationToken.encode(nextCursor(results)));
	}

	private static Long nextCursor(List<ProductSellerTuple> results) {
		if (results.size() <= PAGE_SIZE) {
			return null;
		}

		return results.get(results.size() - 1).product().getDataKey();
	}
}
