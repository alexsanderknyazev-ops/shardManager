package ru.shard.shard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.shard.shard.model.Credit;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface CreditRepository extends JpaRepository<Credit, Long> {
    Optional<Credit> findByContractNumber(String contractNumber);

    @Query("SELECT COALESCE(SUM(c.amount), 0) FROM Credit c WHERE c.status = ru.shard.shard.model.Credit.CreditStatus.ACTIVE")
    BigDecimal sumActiveCreditsAmount();

    boolean existsByContractNumber(String contractNumber);

    @Query("SELECT c FROM Credit c LEFT JOIN FETCH c.client WHERE c.id = :id")
    Optional<Credit> findByIdWithClient(@Param("id") Long id);

    @Query("SELECT c FROM Credit c LEFT JOIN FETCH c.client WHERE c.contractNumber = :contractNumber")
    Optional<Credit> findByContractNumberWithClient(@Param("contractNumber") String contractNumber);
}
