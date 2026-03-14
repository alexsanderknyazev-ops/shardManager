package ru.shard.shard.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.shard.shard.controller.dto.CreditDto;
import ru.shard.shard.model.Credit;
import ru.shard.shard.service.CreditService;

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
    public void deleteCreditById(@PathVariable Long creditId) {
        log.info("Удаление кредита по ID: {}", creditId);
        creditService.deleteCredit(creditId);
    }
    @PostMapping("/create")
    public Credit createCredit(@Valid @RequestBody CreditDto creditDto) {
        return creditService.addCredit(creditDto);
    }
}
