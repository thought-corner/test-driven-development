package com.study.tdd.application.query;

/**
 * 구매자 상품 목록의 한 페이지를 조회하는 질의(query).
 *
 * <p>{@code continuationToken}은 이전 페이지의 마지막 지점을 가리키는 커서(cursor)를
 * 인코딩한 값이다. {@code null}이거나 빈 문자열이면 첫 번째 페이지를 뜻한다.</p>
 */
public record GetProductPage(String continuationToken) {
}
