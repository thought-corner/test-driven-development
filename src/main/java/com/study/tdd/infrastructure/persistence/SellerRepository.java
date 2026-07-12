package com.study.tdd.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import com.study.tdd.domain.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerRepository extends JpaRepository<Seller, Long> {

    Optional<Seller> findById(UUID id);

    Optional<Seller> findByEmail(String email);
}
