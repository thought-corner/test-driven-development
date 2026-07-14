package com.study.tdd.api;

import java.net.URI;
import java.util.UUID;

import com.study.tdd.api.controller.response.AccessTokenCarrier;
import com.study.tdd.api.controller.response.SellerMeView;
import com.study.tdd.api.controller.response.ShopperMeView;
import com.study.tdd.application.command.CreateSellerCommand;
import com.study.tdd.application.command.CreateShopperCommand;
import com.study.tdd.application.command.RegisterProductCommand;
import com.study.tdd.application.query.IssueSellerToken;
import com.study.tdd.application.query.IssueShopperToken;

import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static java.util.Objects.requireNonNull;
import static com.study.tdd.support.EmailGenerator.generateEmail;
import static com.study.tdd.support.PasswordGenerator.generatePassword;
import static com.study.tdd.support.RegisterProductCommandGenerator.generateRegisterProductCommand;
import static com.study.tdd.support.UsernameGenerator.generateUsername;

/**
 * API 명세 테스트를 위한 시나리오 픽스처(fixture).
 *
 * <p>"가입 → 토큰 발급 → 인증된 요청"처럼 여러 스펙이 반복하던 준비(arrange) 절차를
 * 도메인 언어의 메서드 하나로 끌어올린다. 각 스펙은 저수준 HTTP 호출을 나열하는 대신
 * {@code fixture.createShopperThenIssueToken()}처럼 의도만 드러내는 한 줄로 준비를 마친다.</p>
 *
 * <p>픽스처는 {@code @Scope("prototype")}으로 테스트마다 새로 만들어지며, 자신만의
 * {@link TestRestTemplate}을 소유한다. 따라서 {@link #setShopperAsDefaultUser}처럼
 * 클라이언트에 기본 인증 헤더를 심는 조작이 다른 테스트로 새지 않는다.</p>
 */
public record TestFixture(TestRestTemplate client) {

	public static TestFixture create(Environment environment) {
		int port = environment.getRequiredProperty("local.server.port", Integer.class);
		var builder = new RestTemplateBuilder().rootUri("http://localhost:" + port);
		return new TestFixture(new TestRestTemplate(builder));
	}

	public void createShopper(String email, String username, String password) {
		var command = new CreateShopperCommand(email, username, password);
		ensureSuccessful(
			client.postForEntity("/shopper/signUp", command, Void.class),
			command
		);
	}

	public void createSeller(
		String email,
		String username,
		String password,
		String contactEmail
	) {
		var command = new CreateSellerCommand(email, username, password, contactEmail);
		ensureSuccessful(
			client.postForEntity("/seller/signUp", command, Void.class),
			command
		);
	}

	public String issueShopperToken(String email, String password) {
		AccessTokenCarrier carrier = client.postForObject(
			"/shopper/issueToken",
			new IssueShopperToken(email, password),
			AccessTokenCarrier.class
		);
		return requireNonNull(carrier).accessToken();
	}

	public String issueSellerToken(String email, String password) {
		AccessTokenCarrier carrier = client.postForObject(
			"/seller/issueToken",
			new IssueSellerToken(email, password),
			AccessTokenCarrier.class
		);
		return requireNonNull(carrier).accessToken();
	}

	public String createShopperThenIssueToken() {
		String email = generateEmail();
		String password = generatePassword();
		createShopper(email, generateUsername(), password);
		return issueShopperToken(email, password);
	}

	public String createSellerThenIssueToken() {
		String email = generateEmail();
		String password = generatePassword();
		createSeller(email, generateUsername(), password, generateEmail());
		return issueSellerToken(email, password);
	}

	public void createSellerThenSetAsDefaultUser() {
		String email = generateEmail();
		String password = generatePassword();
		createSeller(email, generateUsername(), password, generateEmail());
		setSellerAsDefaultUser(email, password);
	}

	public void createShopperThenSetAsDefaultUser() {
		String email = generateEmail();
		String password = generatePassword();
		createShopper(email, generateUsername(), password);
		setShopperAsDefaultUser(email, password);
	}

	public UUID registerProduct() {
		return registerProduct(generateRegisterProductCommand());
	}

	public UUID registerProduct(RegisterProductCommand command) {
		ResponseEntity<Void> response = client.postForEntity(
			"/seller/products",
			command,
			Void.class
		);
		URI location = requireNonNull(response.getHeaders().getLocation());
		String path = location.getPath();
		String id = path.substring("/seller/products/".length());
		return UUID.fromString(id);
	}

	public void setShopperAsDefaultUser(String email, String password) {
		setDefaultAuthorization("Bearer " + issueShopperToken(email, password));
	}

	public void setSellerAsDefaultUser(String email, String password) {
		setDefaultAuthorization("Bearer " + issueSellerToken(email, password));
	}

	public ShopperMeView getShopper() {
		return client.getForObject("/shopper/me", ShopperMeView.class);
	}

	public SellerMeView getSeller() {
		return client.getForObject("/seller/me", SellerMeView.class);
	}

	private void setDefaultAuthorization(String authorization) {
		RestTemplate template = client.getRestTemplate();
		template.getInterceptors().add(0, (request, body, execution) -> {
			if (!request.getHeaders().containsHeader("Authorization")) {
				request.getHeaders().add("Authorization", authorization);
			}
			return execution.execute(request, body);
		});
	}

	private void ensureSuccessful(ResponseEntity<Void> response, Object request) {
		if (!response.getStatusCode().is2xxSuccessful()) {
			throw new RuntimeException(
				"Request with " + request
					+ " failed with status code " + response.getStatusCode()
			);
		}
	}
}
