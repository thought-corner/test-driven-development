package com.study.tdd.api;

import com.study.tdd.infrastructure.persistence.ProductRepository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;

public class TestFixtureConfiguration {

	@Bean
	@Scope("prototype")
	TestFixture testFixture(Environment environment, ProductRepository productRepository) {
		return TestFixture.create(environment, productRepository);
	}
}
