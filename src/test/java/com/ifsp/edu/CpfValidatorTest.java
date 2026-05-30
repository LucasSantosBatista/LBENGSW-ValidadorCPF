package com.ifsp.edu;

import com.ifsp.edu.annotation.ValidCpf;
import com.ifsp.edu.exception.CpfInvalidException;
import com.ifsp.edu.service.CpfValidationService;
import com.ifsp.edu.validator.CpfValidator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = { CpfValidationService.class, CpfValidator.class, LocalValidatorFactoryBean.class })
class CpfValidatorTest {

	@Autowired
	private Validator validator;

	@Autowired
	private CpfValidationService service; // Injetado pelo Spring, sem usar "new"

	// DTOs para os testes da anotação
	static class DtoObrigatorioEFormatado {
		@ValidCpf(required = true, formatted = true)
		String cpf;
	}

	static class DtoOpcionalENaoFormatado {
		@ValidCpf(required = false, formatted = false)
		String cpf;
	}

	@Nested
	@DisplayName("1. Testes da Anotação @ValidCpf e CpfValidator")
	class AnnotationTests {

		@Test
		@DisplayName("Deve passar quando CPF é nulo/vazio e required=false")
		void cpfOpcionalVazio() {
			DtoOpcionalENaoFormatado dto = new DtoOpcionalENaoFormatado();
			dto.cpf = "";
			Set<ConstraintViolation<DtoOpcionalENaoFormatado>> violations = validator.validate(dto);
			assertTrue(violations.isEmpty());
		}

		@Test
		@DisplayName("Deve falhar quando CPF é nulo/vazio e required=true")
		void cpfObrigatorioVazio() {
			DtoObrigatorioEFormatado dto = new DtoObrigatorioEFormatado();
			dto.cpf = null;
			Set<ConstraintViolation<DtoObrigatorioEFormatado>> violations = validator.validate(dto);
			assertEquals(1, violations.size());
		}

		@Test
		@DisplayName("Deve falhar se enviar formato especial quando formatted=false")
		void cpfFormatadoQuandoNaoPermitido() {
			DtoOpcionalENaoFormatado dto = new DtoOpcionalENaoFormatado();
			dto.cpf = "042.835.920-70";
			Set<ConstraintViolation<DtoOpcionalENaoFormatado>> violations = validator.validate(dto);
			assertEquals(1, violations.size());
		}
	}

	@Nested
	@DisplayName("2. Testes de Regra de Negócio (CpfValidationService)")
	class ServiceTests {

		@Test
		@DisplayName("Deve aceitar CPFs válidos conhecidos matematicamente")
		void cpfsValidos() {
			// Testamos o CPF fixo que comprovadamente passou
			assertTrue(service.isValid("52998224725"));

			// Geramos mais 3 CPFs válidos dinamicamente para garantir a cobertura completa
			for (int i = 0; i < 3; i++) {
				String cpfGerado = service.gerarCpfValido();
				assertTrue(service.isValid(cpfGerado), "Falhou para o CPF gerado: " + cpfGerado);
			}
		}

		@ParameterizedTest
		@ValueSource(strings = { "11111111111", "12345678900", "1234567890" })
		@DisplayName("Deve rejeitar CPFs matematicamente inválidos")
		void cpfsInvalidos(String cpf) {
			assertFalse(service.isValid(cpf));
		}

		@Test
		@DisplayName("Deve formatar com sucesso um CPF limpo e válido")
		void formatarSucesso() {
			// 1. Usa o próprio gerador do seu service para criar um CPF 100% válido na hora
			String cpfValidoInstante = service.gerarCpfValido();

			// 2. Passa para o formatador
			String formatado = service.formatar(cpfValidoInstante);

			// 3. Verifica se a estrutura da máscara (. . -) foi aplicada nos lugares certos
			assertNotNull(formatado);
			assertEquals(14, formatado.length()); // 11 números + 2 pontos + 1 traço = 14 caracteres
			assertTrue(formatado.matches("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}"));
		}

		@Test
		@DisplayName("Deve lançar CpfInvalidException se tentar formatar CPF nulo")
		void formatarNulo() {
			assertThrows(CpfInvalidException.class, () -> service.formatar(null));
		}

		@Test
		@DisplayName("Deve gerar um CPF válido que passe no próprio teste de validação")
		void gerarCpfValido() {
			String cpfGerado = service.gerarCpfValido();
			assertNotNull(cpfGerado);
			assertEquals(11, cpfGerado.length());
			assertTrue(service.isValid(cpfGerado, false));
		}
	}
}