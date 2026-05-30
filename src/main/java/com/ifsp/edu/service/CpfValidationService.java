package com.ifsp.edu.service;

import org.springframework.stereotype.Service;

import com.ifsp.edu.exception.CpfInvalidException;

@Service
public class CpfValidationService {
    /**
     * Valida CPF
     * @param cpf CPF com ou sem formatacao
     * @param acceptFormatted Se true, aceita formato 123.456.789-09
     */
	public boolean isValid(String cpf, boolean acceptFormatted) {
	    if (cpf == null || cpf.isBlank()) {
	        return false;
	    }

	    // Se NÃO aceita formatado, mas contém caracteres além de números, rejeita direto
	    if (!acceptFormatted && cpf.matches(".*[^0-9].*")) {
	        return false;
	    }

	    // Limpa a string para fazer a validação matemática
	    String cpfLimpo = cpf.replaceAll("[^0-9]", "");

	    // Validação básica de tamanho
	    if (cpfLimpo.length() != 11) {
	        return false;
	    }

	    // Verifica dígitos iguais
	    if (cpfLimpo.matches("(\\d)\\1{10}")) {
	        return false;
	    }

	    // Valida dígitos verificadores
	    return validarDigitos(cpfLimpo);
	}

	public boolean isValid(String cpf) {
	    return isValid(cpf, true); // Por padrão, aceita das duas formas
	}

	private boolean validarDigitos(String cpf) {
	    try {
	        int digito1 = calcularDigito(cpf, 10);
	        int digito2 = calcularDigito(cpf, 11);
	        
	        int digitoInformado1 = Character.getNumericValue(cpf.charAt(9));
	        int digitoInformado2 = Character.getNumericValue(cpf.charAt(10));
	        
	        return digito1 == digitoInformado1 && digito2 == digitoInformado2;
	    } catch (Exception e) {
	        return false;
	    }
	}

	private int calcularDigito(String cpf, int peso) {
	    int soma = 0;
	    for (int i = 0; i < peso - 1; i++) {
	        soma += Character.getNumericValue(cpf.charAt(i)) * (peso - i);
	    }
	    int resto = soma % 11;
	    return resto < 2 ? 0 : 11 - resto;
	}

	/**
	 * Formata CPF
	 */
	public String formatar(String cpf) {
	    // Para garantir, limpamos primeiro para validar e formatar uniformemente
	    if (cpf == null) throw new CpfInvalidException("CPF não pode ser nulo");
	    
	    String cpfLimpo = cpf.replaceAll("[^0-9]", "");
	    
	    if (!isValid(cpfLimpo, false)) { // Valida a versão limpa
	        throw new CpfInvalidException("CPF invalido para formatacao: " + cpf);
	    }
	    
	    return String.format("%s.%s.%s-%s",
	        cpfLimpo.substring(0, 3),
	        cpfLimpo.substring(3, 6),
	        cpfLimpo.substring(6, 9),
	        cpfLimpo.substring(9, 11)
	    );
	}

	/**
	 * Gera CPF valido para testes
	 */
	public String gerarCpfValido() {
	    int[] numeros = new int[9];
	    java.util.Random random = new java.util.Random(); // Melhor que Math.random()
	    
	    for (int i = 0; i < 9; i++) {
	        numeros[i] = random.nextInt(10);
	    }
	    
	    int digito1 = calcularDigitoArray(numeros, 10);
	    int digito2 = calcularDigitoArray(adicionarDigito(numeros, digito1), 11);
	    
	    StringBuilder cpf = new StringBuilder();
	    for (int num : numeros) cpf.append(num);
	    cpf.append(digito1).append(digito2);
	    
	    return cpf.toString();
	}

	private int calcularDigitoArray(int[] numeros, int peso) {
	    int soma = 0;
	    for (int i = 0; i < numeros.length; i++) {
	        soma += numeros[i] * (peso - i);
	    }
	    int resto = soma % 11;
	    return resto < 2 ? 0 : 11 - resto;
	}

	private int[] adicionarDigito(int[] array, int digito) {
	    int[] novo = new int[array.length + 1];
	    System.arraycopy(array, 0, novo, 0, array.length);
	    novo[array.length] = digito;
	    return novo;
	}
}
