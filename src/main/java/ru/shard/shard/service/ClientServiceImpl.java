package ru.shard.shard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.shard.shard.model.Client;
import ru.shard.shard.repository.ClientRepository;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService{
    private final ClientRepository clientRepository;
    @Override
    public Client getClient(Long id) {
        return clientRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Client not")
        );
    }
}
