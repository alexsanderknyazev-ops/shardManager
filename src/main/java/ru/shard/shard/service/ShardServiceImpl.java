package ru.shard.shard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.shard.shard.config.ShardManager;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShardServiceImpl implements ShardService {
    private final ShardManager shardManager;

    @Override
    public String getShardNameByCreditId(Long creditId) {
        return shardManager.determineShardByCreditId(creditId).orElseThrow(
                () -> new RuntimeException("Кредита нет на шардах")
        );
    }

    @Override
    public List<String> getAllShards() {
        return shardManager.getAllShards();
    }

    @Override
    public Optional<String> determineShardByCreditId(Long creditId) {
        return shardManager.determineShardByCreditId(creditId);
    }
}
