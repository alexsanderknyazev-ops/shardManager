package ru.shard.shard.service;

import java.util.List;
import java.util.Optional;

public interface ShardService {

    String getShardNameByCreditId(Long creditId);
    List<String> getAllShards();
    Optional<String> determineShardByCreditId(Long creditId);
}
