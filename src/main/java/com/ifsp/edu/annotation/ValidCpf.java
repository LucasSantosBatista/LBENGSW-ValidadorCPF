package com.ifsp.edu.annotation;

import java.lang.annotation.*;

import com.ifsp.edu.validator.CpfValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CpfValidator.class)
@Documented
public @interface ValidCpf {
	String message() default "CPF invalido";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	boolean formatted() default true; // Aceita CPF formatado?

	boolean required() default true; // Campo obrigatorio?
}
