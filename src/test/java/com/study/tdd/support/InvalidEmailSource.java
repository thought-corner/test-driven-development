package com.study.tdd.support;

import java.lang.annotation.Retention;

import org.junit.jupiter.params.provider.MethodSource;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 무효 이메일 반례 묶음을 주입하는 선언적 파라미터 소스.
 *
 * <p>{@code @MethodSource("com.study.tdd.support.TestDataSource#invalidEmails")}의 긴 문자열
 * 경로를 애노테이션 이름 하나로 감싼다. 스펙에서는 {@code @ParameterizedTest} 옆에
 * {@code @InvalidEmailSource} 한 줄만 붙이면 되며, 오타에 취약한 경로 문자열이 사라지고
 * 검증 의도가 이름으로 드러난다. 정책의 반례가 한곳({@link TestDataSource})에 모여 있어
 * 이메일 정책이 바뀌면 반례 목록도 한곳만 고치면 된다.</p>
 */
@Retention(RUNTIME)
@MethodSource("com.study.tdd.support.TestDataSource#invalidEmails")
public @interface InvalidEmailSource {
}
