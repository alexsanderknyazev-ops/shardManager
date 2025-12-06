package ru.shard.shard.service;

import ru.shard.shard.model.Client;
import ru.shard.shard.model.Credit;

public interface CreditService {

    Credit getCredit(Long id);
    Client setCredit(Long id, Long clientId);
}
