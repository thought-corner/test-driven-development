package com.study.tdd.application.query;

import com.study.tdd.domain.Product;
import com.study.tdd.domain.Seller;

/**
 * 상품과 그 상품을 등록한 판매자를 함께 실어 나르는 조회 전용 투영(projection).
 *
 * <p>{@code JOIN}으로 얻은 두 엔티티 쌍을 그대로 담기만 하는 순수 데이터 묶음이며,
 * 뷰(view)로의 변환이나 커서 계산 같은 책임은 갖지 않는다.</p>
 */
public record ProductSellerTuple(Product product, Seller seller) {
}
