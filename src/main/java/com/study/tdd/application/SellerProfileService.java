package com.study.tdd.application;

import java.util.Optional;
import java.util.UUID;

import com.study.tdd.domain.Seller;
import com.study.tdd.infrastructure.persistence.SellerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SellerProfileService {

	private final SellerRepository repository;

	@Autowired
	public SellerProfileService(SellerRepository repository) {
		this.repository = repository;
	}

	public Optional<Seller> findSeller(UUID sellerId) {
		return repository.findById(sellerId);
	}
}
