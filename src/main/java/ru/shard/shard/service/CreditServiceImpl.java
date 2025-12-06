package ru.shard.shard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.shard.shard.config.ShardManager;
import ru.shard.shard.model.Client;
import ru.shard.shard.model.Credit;
import ru.shard.shard.repository.CreditRepository;


@Service
@RequiredArgsConstructor
public class CreditServiceImpl implements CreditService {

    private final ShardManager shardManager;

    private final CreditRepository creditRepository;

    @Override
    public Credit getCredit(Long id) {
        String shardOpt = shardManager.determineShardByCreditId(id).orElseThrow(
                () -> new RuntimeException("Shard not")
        );
        Credit credit = shardManager.findCreditWithClientInSpecificShard(id, shardOpt).orElseThrow(
                () -> new RuntimeException("credit not")
        );
        return credit;
    }
    @Override
    public Client setCredit(Long id, Long clientId){
        String shardOpt = shardManager.determineShardByCreditId(id).orElseThrow(
                () -> new RuntimeException("Shard not")
        );
        Client client = (Client) shardManager.findEntityInSpecificShard("client", clientId, shardOpt).get();
        return client;
    }
}
