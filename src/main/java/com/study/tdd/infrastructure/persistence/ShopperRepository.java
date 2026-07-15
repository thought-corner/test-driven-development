package com.study.tdd.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import com.study.tdd.domain.Shopper;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopperRepository extends JpaRepository<Shopper, Long> {

	Optional<Shopper> findById(UUID id);

	Optional<Shopper> findByEmail(String email);
}
