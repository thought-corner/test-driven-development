package com.study.tdd.support;

import java.lang.annotation.Retention;

import org.junit.jupiter.params.provider.MethodSource;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 무효 비밀번호 반례 묶음을 주입하는 선언적 파라미터 소스.
 *
 * <p>{@code @MethodSource("com.study.tdd.support.TestDataSource#invalidPasswords")}의 긴 문자열
 * 경로를 애노테이션 이름 하나로 감싼다. 스펙에서는 {@code @ParameterizedTest} 옆에
 * {@code @InvalidPasswordSource} 한 줄만 붙이면 되며, 오타에 취약한 경로 문자열이 사라지고
 * 검증 의도가 이름으로 드러난다. 비밀번호 정책의 반례가 한곳({@link TestDataSource})에 모여
 * 있어, 그 정책을 검증하는 모든 스펙(판매자·구매자 가입 등)이 같은 목록을 공유한다.</p>
 */
@Retention(RUNTIME)
@MethodSource("com.study.tdd.support.TestDataSource#invalidPasswords")
public @interface InvalidPasswordSource {
}
