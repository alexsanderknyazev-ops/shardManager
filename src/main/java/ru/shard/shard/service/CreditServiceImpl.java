package ru.shard.shard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.shard.shard.config.RoutingDataSource;
import ru.shard.shard.config.ShardManager;
import ru.shard.shard.controller.dto.CreditDto;
import ru.shard.shard.model.Client;
import ru.shard.shard.model.Credit;
import ru.shard.shard.repository.CreditRepository;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class CreditServiceImpl implements CreditService {

    private final ShardService shardService;
    private final CreditRepository creditRepository;
    private final ClientService clientService;
    @Override
    public Credit getCredit(Long id) {
        String shardOpt = getShardName(id);
        RoutingDataSource.setCurrentShard(shardOpt);
        try {
            Credit creditOpt = creditRepository.findByIdWithClient(id).orElseThrow(
                    () -> new RuntimeException("Кредит не найден")
            );
            return creditOpt;

        } finally {
            RoutingDataSource.clearCurrentShard();
        }
    }

    @Override
    public Client setCredit(Long id, Long clientId) {
        String shardOpt = getShardName(id);
        RoutingDataSource.setCurrentShard(shardOpt);

        return null;
    }

    @Override
    public Credit addCredit(CreditDto creditDto) {
        String shardOpt = "shard02";
        RoutingDataSource.setCurrentShard(shardOpt);
        Credit credit = new Credit();
        try {
             credit = Credit.builder()
                    .client(clientService.getClient(creditDto.getClient()))
                    .contractNumber(creditDto.getContractNumber())
                    .amount(creditDto.getAmount())
                    .interestRate(creditDto.getInterestRate())
                    .termMonths(creditDto.getTermMonths())
                    .startDate(creditDto.getStartDate())
                    .endDate(creditDto.getEndDate())
                    .status(Credit.CreditStatus.ACTIVE)
                    .createdAt(creditDto.getCreatedAt())
                    .build();
            creditRepository.save(credit);
        }finally {
            RoutingDataSource.clearCurrentShard();
        }
        return credit;
    }

    private String getShardName(Long id){
        return shardService.getShardNameByCreditId(id);
    }
}
