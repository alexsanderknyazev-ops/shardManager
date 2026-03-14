package ru.shard.shard.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CreditTest {

	@Test
	void isActiveReturnsTrueWhenStatusActive() {
		Credit credit = Credit.builder()
				.status(Credit.CreditStatus.ACTIVE)
				.build();
		assertThat(credit.isActive()).isTrue();
	}

	@Test
	void isActiveReturnsFalseWhenStatusClosed() {
		Credit credit = Credit.builder()
				.status(Credit.CreditStatus.CLOSED)
				.build();
		assertThat(credit.isActive()).isFalse();
	}

	@Test
	void getMonthlyPaymentReturnsZeroWhenAmountNull() {
		Credit credit = Credit.builder()
				.amount(null)
				.interestRate(BigDecimal.TEN)
				.termMonths(12)
				.build();
		assertThat(credit.getMonthlyPayment()).isEqualByComparingTo(BigDecimal.ZERO);
	}

	@Test
	void getMonthlyPaymentReturnsPositiveForValidData() {
		Credit credit = Credit.builder()
				.amount(BigDecimal.valueOf(100_000))
				.interestRate(BigDecimal.valueOf(12))
				.termMonths(12)
				.build();
		assertThat(credit.getMonthlyPayment()).isPositive();
	}

	@Test
	void getTotalAmountEqualsMonthlyPaymentTimesMonths() {
		Credit credit = Credit.builder()
				.amount(BigDecimal.valueOf(100_000))
				.interestRate(BigDecimal.valueOf(12))
				.termMonths(12)
				.build();
		BigDecimal total = credit.getMonthlyPayment().multiply(BigDecimal.valueOf(12));
		assertThat(credit.getTotalAmount()).isEqualByComparingTo(total);
	}
}
