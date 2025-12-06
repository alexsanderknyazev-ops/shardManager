package ru.shard.shard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.shard.shard.model.Client;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByPassportNumber(String passportNumber);

    boolean existsByPassportNumber(String passportNumber);
}
