package com.study.tdd.application;

import java.time.LocalDateTime;
import java.util.UUID;

import com.study.tdd.application.command.RegisterProductCommand;
import com.study.tdd.application.exception.BusinessException;
import com.study.tdd.application.exception.ProductErrorCode;
import com.study.tdd.domain.Product;
import com.study.tdd.infrastructure.persistence.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.time.ZoneOffset.UTC;
import static com.study.tdd.domain.validation.ProductPropertyValidator.isImageUriValid;

@Service
public class SellerProductRegistrationService {

	private final ProductRepository repository;

	@Autowired
	public SellerProductRegistrationService(ProductRepository repository) {
		this.repository = repository;
	}

	public UUID registerProduct(UUID sellerId, RegisterProductCommand command) {
		if (!isImageUriValid(command.imageUri())) {
			throw new BusinessException(ProductErrorCode.INVALID_IMAGE_URI);
		}

		UUID id = UUID.randomUUID();
		repository.save(createProduct(id, sellerId, command));
		return id;
	}

	private static Product createProduct(
		UUID id,
		UUID sellerId,
		RegisterProductCommand command
	) {
		var product = new Product();
		product.setId(id);
		product.setSellerId(sellerId);
		product.setName(command.name());
		product.setImageUri(command.imageUri());
		product.setDescription(command.description());
		product.setPriceAmount(command.priceAmount());
		product.setStockQuantity(command.stockQuantity());
		product.setRegisteredTimeUtc(LocalDateTime.now(UTC));
		return product;
	}
}
