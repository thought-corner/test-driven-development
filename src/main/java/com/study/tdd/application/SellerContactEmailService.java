package com.study.tdd.application;

import java.util.UUID;

import com.study.tdd.application.command.ChangeContactEmailCommand;
import com.study.tdd.application.exception.BusinessException;
import com.study.tdd.application.exception.UserErrorCode;
import com.study.tdd.domain.Seller;
import com.study.tdd.infrastructure.persistence.SellerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.study.tdd.domain.validation.UserPropertyValidator.isEmailValid;

@Service
public class SellerContactEmailService {

	private final SellerRepository repository;

	@Autowired
	public SellerContactEmailService(SellerRepository repository) {
		this.repository = repository;
	}

	public void changeContactEmail(UUID sellerId, ChangeContactEmailCommand command) {
		if (!isEmailValid(command.contactEmail())) {
			throw new BusinessException(UserErrorCode.INVALID_COMMAND);
		}

		Seller seller = repository.findById(sellerId).orElseThrow();
		seller.setContactEmail(command.contactEmail());
		repository.save(seller);
	}
}
