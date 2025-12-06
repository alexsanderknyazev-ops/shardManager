package ru.shard.shard.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "credits")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Credit {

    public enum CreditStatus {
        ACTIVE, CLOSED, OVERDUE, PENDING
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = "contract_number", nullable = false, unique = true, length = 50)
    private String contractNumber;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "interest_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal interestRate;

    @Column(name = "term_months", nullable = false)
    private Integer termMonths;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private CreditStatus status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Transient
    public BigDecimal getMonthlyPayment() {
        if (amount == null || interestRate == null || termMonths == null || termMonths == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal monthlyRate = interestRate.divide(BigDecimal.valueOf(1200), 10, BigDecimal.ROUND_HALF_UP);
        BigDecimal numerator = monthlyRate.multiply(
                BigDecimal.ONE.add(monthlyRate).pow(termMonths)
        );
        BigDecimal denominator = BigDecimal.ONE.add(monthlyRate).pow(termMonths).subtract(BigDecimal.ONE);

        return amount.multiply(numerator).divide(denominator, 2, BigDecimal.ROUND_HALF_UP);
    }

    @Transient
    public BigDecimal getTotalAmount() {
        return getMonthlyPayment().multiply(BigDecimal.valueOf(termMonths));
    }

    // Проверка, активен ли кредит
    @Transient
    public boolean isActive() {
        return CreditStatus.ACTIVE.equals(status);
    }

    @Override
    public String toString() {
        return "Credit{" +
                "id=" + id +
                ", clientId=" + (client != null ? client.getId() : null) +
                ", contractNumber='" + contractNumber + '\'' +
                ", amount=" + amount +
                ", interestRate=" + interestRate +
                ", termMonths=" + termMonths +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}
