package com.study.tdd.application.query;

import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 페이지 커서(cursor)를 클라이언트에 노출할 문자열로 감추고 되돌리는 코덱(codec).
 *
 * <p>내부적으로 커서는 {@code Product.dataKey}(단조 증가하는 {@code Long})지만,
 * 이를 그대로 노출하면 저장소 구조가 새어 나간다. 그래서 URL-safe Base64로
 * 인코딩한 불투명 토큰(opaque token)으로 주고받는다.</p>
 *
 * <p>이 타입의 유일한 책임은 <strong>커서 ↔ 토큰의 상호 변환</strong>이다.
 * 페이지를 어떻게 읽고 자르는지는 관여하지 않는다.</p>
 */
public final class ContinuationToken {

	private ContinuationToken() {
	}

	/**
	 * 토큰을 커서로 되돌린다. {@code null}이거나 빈 문자열이면 커서 없음({@code null})으로 본다.
	 */
	public static Long decode(String token) {
		if (token == null || token.isBlank()) {
			return null;
		}

		byte[] data = Base64.getUrlDecoder().decode(token);
		return Long.parseLong(new String(data, UTF_8));
	}

	/**
	 * 커서를 토큰으로 감춘다. 다음 페이지가 없어 커서가 {@code null}이면 토큰도 {@code null}이다.
	 */
	public static String encode(Long cursor) {
		if (cursor == null) {
			return null;
		}

		byte[] data = cursor.toString().getBytes(UTF_8);
		return Base64.getUrlEncoder().encodeToString(data);
	}
}
