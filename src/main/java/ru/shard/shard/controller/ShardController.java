package ru.shard.shard.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.shard.shard.service.ShardService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shard")
@RequiredArgsConstructor
public class ShardController {

    private final ShardService shardService;

    @GetMapping
    public List<String> getAllShards() {
        return shardService.getAllShards();
    }

    @GetMapping("/credit/{creditId}")
    public Map<String, String> getShardByCreditId(@PathVariable Long creditId) {
        String shard = shardService.getShardNameByCreditId(creditId);
        return Map.of("creditId", creditId.toString(), "shard", shard);
    }
}
