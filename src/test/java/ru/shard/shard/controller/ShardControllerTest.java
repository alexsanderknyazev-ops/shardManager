package ru.shard.shard.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import ru.shard.shard.exception.CreditNotFoundException;
import ru.shard.shard.exception.GlobalExceptionHandler;
import ru.shard.shard.service.ShardService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ShardController.class)
@Import(GlobalExceptionHandler.class)
class ShardControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ShardService shardService;

	@Test
	void getAllShardsReturnsList() throws Exception {
		when(shardService.getAllShards()).thenReturn(List.of("shard02", "shard03"));

		mockMvc.perform(get("/api/shard"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0]").value("shard02"))
				.andExpect(jsonPath("$[1]").value("shard03"));
	}

	@Test
	void getShardByCreditIdReturnsShard() throws Exception {
		when(shardService.getShardNameByCreditId(1L)).thenReturn("shard02");

		mockMvc.perform(get("/api/shard/credit/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.creditId").value("1"))
				.andExpect(jsonPath("$.shard").value("shard02"));
	}

	@Test
	void getShardByCreditIdReturns404WhenNotFound() throws Exception {
		when(shardService.getShardNameByCreditId(999L)).thenThrow(new CreditNotFoundException(999L));

		mockMvc.perform(get("/api/shard/credit/999"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.error").value("NOT_FOUND"));
	}
}
