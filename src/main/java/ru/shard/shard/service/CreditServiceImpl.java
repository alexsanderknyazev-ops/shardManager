package ru.shard.shard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.shard.shard.config.WithShardRouting;
import ru.shard.shard.controller.dto.CreditDto;
import ru.shard.shard.model.Client;
import ru.shard.shard.model.Credit;
import ru.shard.shard.repository.CreditRepository;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class CreditServiceImpl implements CreditService {

    private final CreditRepository creditRepository;
    private final ClientService clientService;

    @Override
    @WithShardRouting(byId = true)
    public Credit getCredit(Long id) {
        return creditRepository.findByIdWithClient(id).orElseThrow(
                () -> new RuntimeException("Кредит не найден")
        );
    }

    @Override
    @WithShardRouting(byId = true)
    public Client setCredit(Long id, Long clientId) {
        // Логика метода
        return null;
    }

    @Override
    @WithShardRouting(shard = "shard03")
    public Credit addCredit(CreditDto creditDto) {
        Credit credit = Credit.builder()
                .client(clientService.getClient(creditDto.getClient()))
                .contractNumber(creditDto.getContractNumber())
                .amount(creditDto.getAmount())
                .interestRate(creditDto.getInterestRate())
                .termMonths(creditDto.getTermMonths())
                .startDate(creditDto.getStartDate())
                .endDate(creditDto.getEndDate())
                .status(Credit.CreditStatus.ACTIVE)
                .createdAt(creditDto.getCreatedAt() != null ?
                        creditDto.getCreatedAt() : LocalDateTime.now())
                .build();

        Credit savedCredit = creditRepository.save(credit);
        return savedCredit;
    }
}