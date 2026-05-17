package com.ifsp.edu;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ifsp.edu.service.CpfValidationService;

@Configuration
public class CpfValidatorAutoConfiguration {
	@Bean
	@ConditionalOnMissingBean
	public CpfValidationService cpfValidationService() {
		return new CpfValidationService();
	}
}