package com.ifsp.edu.service;

import org.springframework.stereotype.Service;
import com.ifsp.edu.exception.CpfInvalidException;

@Service
public class CpfValidationService {

	public boolean isValid(String cpf, boolean acceptFormatted) {
		if (cpf == null || cpf.isBlank()) {
			return false;
		}

		if (!acceptFormatted && cpf.matches(".*[^0-9].*")) {
			return false;
		}

		String cpfLimpo = cpf.trim().replaceAll("[^0-9]", "");

		if (cpfLimpo.length() != 11 || cpfLimpo.matches("(\\d)\\1{10}")) {
			return false;
		}

		return validarDigitos(cpfLimpo);
	}

	public boolean isValid(String cpf) {
		return isValid(cpf, true);
	}

	private boolean validarDigitos(String cpf) {
		try {
			// 1. Convertemos a string de 11 caracteres em um array de inteiros
			int[] d = new int[11];
			for (int i = 0; i < 11; i++) {
				d[i] = Character.getNumericValue(cpf.charAt(i));
			}

			// 2. Cálculo do primeiro dígito (peso de 10 a 2)
			int soma1 = 0;
			for (int i = 0; i < 9; i++) {
				soma1 += d[i] * (10 - i);
			}
			int resto1 = soma1 % 11;
			int digito1 = (resto1 < 2) ? 0 : (11 - resto1);

			// 3. Cálculo do segundo dígito (peso de 11 a 2)
			int soma2 = 0;
			for (int i = 0; i < 10; i++) {
				soma2 += d[i] * (11 - i);
			}
			int resto2 = soma2 % 11;
			int digito2 = (resto2 < 2) ? 0 : (11 - resto2);

			// 4. Validação final: os dígitos calculados batem com os informados?
			return (d[9] == digito1 && d[10] == digito2);

		} catch (Exception e) {
			return false;
		}
	}

	private int calcularDigito(String str, int peso) {
		int soma = 0;
		for (int i = 0; i < str.length(); i++) {
			soma += Character.getNumericValue(str.charAt(i)) * (peso - i);
		}
		int resto = soma % 11;
		return resto < 2 ? 0 : 11 - resto;
	}

	public String formatar(String cpf) {
		if (cpf == null) {
			throw new CpfInvalidException("CPF não pode ser nulo");
		}

		String cpfLimpo = cpf.trim().replaceAll("[^0-9]", "");

		if (!isValid(cpfLimpo, true)) {
			throw new CpfInvalidException("CPF invalido para formatacao: " + cpf);
		}

		return String.format("%s.%s.%s-%s", cpfLimpo.substring(0, 3), cpfLimpo.substring(3, 6),
				cpfLimpo.substring(6, 9), cpfLimpo.substring(9, 11));
	}

	/**
	 * Gera CPF valido para testes
	 */
	public String gerarCpfValido() {
		java.util.Random random = new java.util.Random();
		StringBuilder sb = new StringBuilder();

		// 1. Gera os 9 primeiros dígitos aleatórios
		for (int i = 0; i < 9; i++) {
			sb.append(random.nextInt(10));
		}

		// 2. Calcula o primeiro dígito usando a regra oficial
		int soma1 = 0;
		for (int i = 0; i < 9; i++) {
			soma1 += Character.getNumericValue(sb.charAt(i)) * (10 - i);
		}
		int resto1 = soma1 % 11;
		int digito1 = (resto1 < 2) ? 0 : (11 - resto1);
		sb.append(digito1);

		// 3. Calcula o segundo dígito usando a regra oficial
		int soma2 = 0;
		for (int i = 0; i < 10; i++) {
			soma2 += Character.getNumericValue(sb.charAt(i)) * (11 - i);
		}
		int resto2 = soma2 % 11;
		int digito2 = (resto2 < 2) ? 0 : (11 - resto2);
		sb.append(digito2);

		return sb.toString();
	}
}