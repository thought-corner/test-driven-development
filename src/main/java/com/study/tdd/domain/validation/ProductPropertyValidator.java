package com.study.tdd.domain.validation;

import java.net.URI;

public class ProductPropertyValidator {

	public static boolean isImageUriValid(String imageUri) {
		if (imageUri == null) {
			return false;
		}

		try {
			return URI.create(imageUri).getHost() != null;
		} catch (IllegalArgumentException exception) {
			return false;
		}
	}
}
