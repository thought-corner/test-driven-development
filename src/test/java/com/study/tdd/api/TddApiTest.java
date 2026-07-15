package com.study.tdd.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.study.tdd.TddApplication;
import com.study.tdd.support.TestPasswordEncoderConfiguration;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * API 명세 테스트 전용 합성 어노테이션(composed annotation).
 *
 * <p>모든 API 스펙 클래스가 반복하던 테스트 부트스트랩 구성을 한곳에 묶는다.
 * 스펙 클래스는 {@code @TddApiTest} 하나만 붙이면 된다.</p>
 *
 * <h2>묶인 구성</h2>
 * <ul>
 *   <li>{@code webEnvironment = RANDOM_PORT} — 실제 톰캣을 임의 포트로 기동하여
 *       필터·시큐리티·직렬화까지 포함한 진짜 HTTP 경로를 검증한다.</li>
 *   <li>{@link TestPasswordEncoderConfiguration} — 운영용 인코더 대신 빠른 인코더를
 *       {@code @Primary}로 주입해 테스트 속도를 확보한다.</li>
 *   <li>{@link TestFixtureConfiguration} — "가입 → 토큰 발급 → 인증된 요청" 준비 절차를
 *       한 줄로 끌어올린 {@link TestFixture}를 테스트마다 새로({@code prototype}) 주입한다.</li>
 *   <li>{@code @AutoConfigureTestRestTemplate} — Spring Boot 4.1에서 분리된
 *       {@code TestRestTemplate} 빈을 등록한다.</li>
 * </ul>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(
        classes = {
                TddApplication.class,
                TestPasswordEncoderConfiguration.class,
                TestFixtureConfiguration.class
        },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureTestRestTemplate
public @interface TddApiTest {
}
