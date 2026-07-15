package com.study.tdd.api.seller.changecontactemail;

import com.study.tdd.api.TddApiTest;
import com.study.tdd.api.TestFixture;
import com.study.tdd.api.controller.response.SellerMeView;
import com.study.tdd.application.command.ChangeContactEmailCommand;
import com.study.tdd.support.InvalidEmailSource;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static com.study.tdd.support.EmailGenerator.generateEmail;

/**
 * {@code POST /seller/changeContactEmail} 엔드포인트의 명세(specification)를 검증하는 통합 테스트.
 *
 * <p>인증된 판매자가 자신의 문의 이메일 주소를 변경하는 계약을 다룬다. 준비(arrange)는
 * {@link TestFixture}로 "가입 → 토큰 발급 → 기본 사용자 설정"까지 끌어올려, 각 케이스는
 * 의도만 드러내는 몇 줄로 상황을 구성한다.</p>
 */
@TddApiTest
@DisplayName("POST /seller/changeContactEmail")
public class POST_specs {

	@Test
	void 올바르게_요청하면_204_No_Content_상태코드를_반환한다(
		@Autowired TestFixture fixture
	) {
		// Arrange
		fixture.createSellerThenSetAsDefaultUser();
		var command = new ChangeContactEmailCommand(generateEmail());

		// Act
		ResponseEntity<Void> response = fixture.client().postForEntity(
			"/seller/changeContactEmail",
			command,
			Void.class
		);

		// Assert
		assertThat(response.getStatusCode().value()).isEqualTo(204);
	}

	@ParameterizedTest
	@InvalidEmailSource
	void contactEmail_속성이_올바르게_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
		String contactEmail,
		@Autowired TestFixture fixture
	) {
		// Arrange
		fixture.createSellerThenSetAsDefaultUser();
		var command = new ChangeContactEmailCommand(contactEmail);

		// Act
		ResponseEntity<Void> response = fixture.client().postForEntity(
			"/seller/changeContactEmail",
			command,
			Void.class
		);

		// Assert
		assertThat(response.getStatusCode().value()).isEqualTo(400);
	}

	@Test
	void 문의_이메일_주소를_올바르게_변경한다(
		@Autowired TestFixture fixture
	) {
		// Arrange
		fixture.createSellerThenSetAsDefaultUser();
		String newContactEmail = generateEmail();

		// Act
		fixture.client().postForEntity(
			"/seller/changeContactEmail",
			new ChangeContactEmailCommand(newContactEmail),
			Void.class
		);

		// Assert
		SellerMeView actual = fixture.getSeller();
		assertThat(actual.contactEmail()).isEqualTo(newContactEmail);
	}
}
