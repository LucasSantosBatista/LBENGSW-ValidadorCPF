package com.ifsp.edu.validator;

import org.springframework.beans.factory.annotation.Autowired;

import com.ifsp.edu.annotation.ValidCpf;
import com.ifsp.edu.service.CpfValidationService;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CpfValidator implements ConstraintValidator<ValidCpf, String> {
	@Autowired
	private CpfValidationService validationService;
	private boolean formatted;
	private boolean required;

	@Override
	public void initialize(ValidCpf annotation) {
		this.formatted = annotation.formatted();
		this.required = annotation.required();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		// Campo opcional e vazio
		if (!required && (value == null || value.isBlank())) {
			return true;
		}
		// Campo obrigatorio e vazio
		if (required && (value == null || value.isBlank())) {
			return false;
		}
		// Valida CPF
		return validationService.isValid(value, formatted);
	}
}
