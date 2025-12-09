package ru.shard.shard.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.shard.shard.controller.dto.CreditDto;
import ru.shard.shard.model.Credit;
import ru.shard.shard.service.CreditService;

import java.util.Optional;

@RestController
@RequestMapping("/api/credit")
@RequiredArgsConstructor
@Slf4j
public class CreditController {

    private final CreditService creditService;

    @GetMapping("/{creditId}")
    public Credit getCreditId(@PathVariable Long creditId) {
        log.info("Поиск кредита ID: {}", creditId);
        return creditService.getCredit(creditId);
    }
    @DeleteMapping("/{creditId}")
    public Boolean deleteCreditById(@PathVariable Long creditId){
        log.info("Удаление кредита по ID: {}", creditId);
        return null;
    }
    @PostMapping("/create")
    public Credit createCredit(@RequestBody CreditDto creditDto){
        return creditService.addCredit(creditDto);
    }
}
