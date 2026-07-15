package com.study.tdd.application;

import java.util.Optional;
import java.util.UUID;

import com.study.tdd.domain.Shopper;
import com.study.tdd.infrastructure.persistence.ShopperRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShopperProfileService {

	private final ShopperRepository repository;

	@Autowired
	public ShopperProfileService(ShopperRepository repository) {
		this.repository = repository;
	}

	public Optional<Shopper> findShopper(UUID shopperId) {
		return repository.findById(shopperId);
	}
}
