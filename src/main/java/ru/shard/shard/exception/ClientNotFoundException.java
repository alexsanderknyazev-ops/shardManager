package ru.shard.shard.exception;

public class ClientNotFoundException extends RuntimeException {

    private final Long clientId;

    public ClientNotFoundException(Long clientId) {
        super("Клиент не найден: id=" + clientId);
        this.clientId = clientId;
    }

    public Long getClientId() {
        return clientId;
    }
}
