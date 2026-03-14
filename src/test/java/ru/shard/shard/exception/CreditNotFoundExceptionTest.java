package ru.shard.shard.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CreditNotFoundExceptionTest {

	@Test
	void messageContainsCreditId() {
		CreditNotFoundException e = new CreditNotFoundException(42L);
		assertThat(e.getMessage()).contains("42");
		assertThat(e.getCreditId()).isEqualTo(42L);
	}
}
