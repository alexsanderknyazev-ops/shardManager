package ru.shard.shard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.shard.shard.config.RoutingDataSource;
import ru.shard.shard.config.ShardManager;
import ru.shard.shard.config.WithShardRouting;
import ru.shard.shard.controller.dto.CreditDto;
import ru.shard.shard.exception.CreditNotFoundException;
import ru.shard.shard.model.Client;
import ru.shard.shard.model.Credit;
import ru.shard.shard.repository.CreditRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CreditServiceImpl implements CreditService {

    private final CreditRepository creditRepository;
    private final ClientService clientService;
    private final ShardManager shardManager;

    @Override
    @WithShardRouting(byId = true)
    public Credit getCredit(Long id) {
        return creditRepository.findByIdWithClient(id).orElseThrow(
                () -> new CreditNotFoundException(id)
        );
    }

    @Override
    @WithShardRouting(byId = true)
    public Client setCredit(Long id, Long clientId) {
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
        String currentShard = RoutingDataSource.getCurrentShard();
        if (currentShard != null) {
            shardManager.registerCreditShard(savedCredit.getId(), currentShard);
        }
        return savedCredit;
    }

    @Override
    @WithShardRouting(byId = true)
    public void deleteCredit(Long id) {
        creditRepository.deleteById(id);
        shardManager.unregisterCreditShard(id);
    }
}