package com.study.tdd.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.study.tdd.application.query.ProductSellerTuple;
import com.study.tdd.domain.Product;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

	Optional<Product> findById(UUID id);

	List<Product> findBySellerId(UUID sellerId);

	/**
	 * 커서 이하의 상품을 등록 시점 역순(= {@code dataKey} 내림차순)으로 판매자와 함께 조회한다.
	 *
	 * <p>{@code cursor}가 {@code null}이면 첫 번째 페이지를 뜻한다. 다음 페이지 존재 여부를
	 * 판별할 수 있도록, 호출부는 페이지 크기보다 하나 더 많이 요청한다({@code Pageable}).</p>
	 */
	@Query("""
		SELECT new com.study.tdd.application.query.ProductSellerTuple(p, s)
		FROM Product p
		JOIN Seller s ON p.sellerId = s.id
		WHERE :cursor IS NULL OR p.dataKey <= :cursor
		ORDER BY p.dataKey DESC
		""")
	List<ProductSellerTuple> findProductPage(@Param("cursor") Long cursor, Pageable pageable);
}
