package ru.shard.shard.controller.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CreditDto {
    private Long client;
    private String contractNumber;
    private BigDecimal amount;
    private BigDecimal interestRate;
    private Integer termMonths;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createdAt;
}
