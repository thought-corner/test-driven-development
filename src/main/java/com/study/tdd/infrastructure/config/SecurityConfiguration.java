package com.study.tdd.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.oauth2.core.authorization.OAuth2AuthorizationManagers.hasScope;

@Configuration
public class SecurityConfiguration {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http, JwtDecoder jwtDecoder) throws Exception {
		return http
			.csrf(AbstractHttpConfigurer::disable)
			.oauth2ResourceServer(configurer -> configurer.jwt(jwt -> jwt.decoder(jwtDecoder)))
			.authorizeHttpRequests(requests -> requests
				.requestMatchers("/seller/signUp").permitAll()
				.requestMatchers("/seller/issueToken").permitAll()
				.requestMatchers("/shopper/signUp").permitAll()
				.requestMatchers("/shopper/issueToken").permitAll()
				.requestMatchers("/seller/me").access(hasScope("seller"))
				.requestMatchers("/seller/products", "/seller/products/**").access(hasScope("seller"))
				.requestMatchers("/shopper/me").access(hasScope("shopper"))
				.requestMatchers("/shopper/products").access(hasScope("shopper"))
				.anyRequest().authenticated()
			)
			.build();
	}
}
