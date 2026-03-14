package ru.shard.shard.exception;

public class CreditNotFoundException extends RuntimeException {

    private final Long creditId;

    public CreditNotFoundException(Long creditId) {
        super("Кредит не найден: id=" + creditId);
        this.creditId = creditId;
    }

    public Long getCreditId() {
        return creditId;
    }
}
