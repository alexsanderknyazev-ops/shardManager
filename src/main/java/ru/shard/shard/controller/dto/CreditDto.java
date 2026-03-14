package ru.shard.shard.controller.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CreditDto {

    @NotNull(message = "client обязателен")
    private Long client;

    @NotBlank(message = "contractNumber обязателен")
    @Size(max = 50)
    private String contractNumber;

    @NotNull
    @DecimalMin(value = "0.01", message = "amount должен быть положительным")
    private BigDecimal amount;

    @NotNull
    @DecimalMin("0")
    @DecimalMax("100")
    private BigDecimal interestRate;

    @NotNull
    @Min(1)
    @Max(600)
    private Integer termMonths;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    private LocalDateTime createdAt;
}
