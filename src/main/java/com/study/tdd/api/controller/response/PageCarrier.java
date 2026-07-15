package com.study.tdd.api.controller.response;

/**
 * 한 페이지의 항목과, 다음 페이지를 가리키는 이어보기 토큰(continuation token)을 함께 싣는 응답 봉투.
 *
 * <p>{@code continuationToken}이 {@code null}이면 더 이상 다음 페이지가 없다는 뜻이다.</p>
 */
public record PageCarrier<T>(T[] items, String continuationToken) {
}
