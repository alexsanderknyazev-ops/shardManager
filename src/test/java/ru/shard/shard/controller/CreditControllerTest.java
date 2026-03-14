package ru.shard.shard.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.shard.shard.controller.dto.CreditDto;
import ru.shard.shard.exception.GlobalExceptionHandler;
import ru.shard.shard.exception.CreditNotFoundException;
import ru.shard.shard.model.Credit;
import ru.shard.shard.service.CreditService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CreditController.class)
@Import(GlobalExceptionHandler.class)
class CreditControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private CreditService creditService;

	@Test
	void getCreditByIdReturns200WhenFound() throws Exception {
		Credit credit = Credit.builder()
				.id(1L)
				.contractNumber("C-001")
				.amount(BigDecimal.valueOf(1000))
				.interestRate(BigDecimal.valueOf(10))
				.termMonths(12)
				.startDate(LocalDate.now())
				.endDate(LocalDate.now().plusMonths(12))
				.status(Credit.CreditStatus.ACTIVE)
				.createdAt(LocalDateTime.now())
				.build();
		when(creditService.getCredit(1L)).thenReturn(credit);

		mockMvc.perform(get("/api/credit/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.contractNumber").value("C-001"));
	}

	@Test
	void getCreditByIdReturns404WhenNotFound() throws Exception {
		when(creditService.getCredit(999L)).thenThrow(new CreditNotFoundException(999L));

		mockMvc.perform(get("/api/credit/999"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.error").value("NOT_FOUND"))
				.andExpect(jsonPath("$.creditId").value(999));
	}

	@Test
	void deleteCreditByIdCallsService() throws Exception {
		mockMvc.perform(delete("/api/credit/1"))
				.andExpect(status().isOk());
		verify(creditService).deleteCredit(1L);
	}

	@Test
	void createCreditWithInvalidDtoReturns400() throws Exception {
		CreditDto dto = new CreditDto();
		dto.setClient(1L);
		dto.setContractNumber("");
		dto.setAmount(BigDecimal.valueOf(-1));

		mockMvc.perform(post("/api/credit/create")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(dto)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("VALIDATION_FAILED"));
	}

	@Test
	void createCreditReturns200AndCallsService() throws Exception {
		CreditDto dto = new CreditDto();
		dto.setClient(1L);
		dto.setContractNumber("C-002");
		dto.setAmount(BigDecimal.valueOf(2000));
		dto.setInterestRate(BigDecimal.valueOf(12));
		dto.setTermMonths(24);
		dto.setStartDate(LocalDate.now());
		dto.setEndDate(LocalDate.now().plusMonths(24));
		Credit saved = Credit.builder()
				.id(2L)
				.contractNumber("C-002")
				.amount(BigDecimal.valueOf(2000))
				.build();
		when(creditService.addCredit(any(CreditDto.class))).thenReturn(saved);

		mockMvc.perform(post("/api/credit/create")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(dto)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(2));
		verify(creditService).addCredit(any(CreditDto.class));
	}
}
