package ru.shard.shard.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.shard.shard.config.ShardManager;
import ru.shard.shard.model.Client;
import ru.shard.shard.model.Credit;
import ru.shard.shard.service.CreditService;

import java.util.Optional;

@RestController
@RequestMapping("/api/shard")
@RequiredArgsConstructor
@Slf4j
public class CreditController {

    private final CreditService creditService;
    /**
     * GET /api/shard/credit/1
     */
    @GetMapping("/credit/{creditId}/{id}")
    public Client getShardByCreditId(@PathVariable Long creditId,
                                     @PathVariable Long id) {
        log.info("Поиск шарда для кредита ID: {}", creditId);

        return creditService.setCredit(creditId, id);
    }
}
