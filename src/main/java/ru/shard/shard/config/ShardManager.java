package ru.shard.shard.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import ru.shard.shard.model.Client;
import ru.shard.shard.model.Credit;
import ru.shard.shard.repository.ClientRepository;
import ru.shard.shard.repository.CreditRepository;

import javax.sql.DataSource;
import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class ShardManager {

    private final Map<String, DataSource> dataSources;

    private final DataSource catalogDataSource;
    private final CreditRepository creditRepository;
    private final ClientRepository clientRepository;

    /**
     * Получить список всех шардов
     */
    public List<String> getAllShards() {
        return List.copyOf(dataSources.keySet());
    }

    /**
     * Определить шард по ID кредита через центральный каталог
     */
    public Optional<String> determineShardByCreditId(Long creditId) {
        if (creditId == null) {
            log.warn("ID кредита не может быть null");
            return Optional.empty();
        }

        log.debug("Поиск шарда для кредита ID: {} в каталоге", creditId);

        JdbcTemplate catalogJdbc = new JdbcTemplate(catalogDataSource);

        try {
            String shardName = catalogJdbc.queryForObject(
                    "SELECT shard_name FROM credit_shard_mapping WHERE credit_id = ?",
                    String.class,
                    creditId
            );

            if (shardName != null) {
                log.debug("✅ Шард найден в каталоге: {} -> {}", creditId, shardName);
                return Optional.of(shardName);
            }

        } catch (Exception e) {
            log.debug("Кредит ID {} не найден в каталоге: {}", creditId, e.getMessage());
        }

        log.debug("Кредит ID {} не найден в каталоге, ищем на шардах...", creditId);
        return findShardOnAllShardsAndUpdateCatalog(creditId);
    }

    /**
     * Найти шард на всех шардах и обновить каталог
     */
    private Optional<String> findShardOnAllShardsAndUpdateCatalog(Long creditId) {
        for (String shardName : getAllShards()) {
            DataSource shardDataSource = dataSources.get(shardName);
            if (shardDataSource == null) continue;

            JdbcTemplate shardJdbc = new JdbcTemplate(shardDataSource);

            try {
                Integer count = shardJdbc.queryForObject(
                        "SELECT COUNT(*) FROM credits WHERE id = ?",
                        Integer.class,
                        creditId
                );

                if (count != null && count > 0) {
                    log.info("✅ Кредит ID {} найден на шарде: {}", creditId, shardName);

                    // Получаем номер договора для полной информации
                    String contractNumber = shardJdbc.queryForObject(
                            "SELECT contract_number FROM credits WHERE id = ?",
                            String.class,
                            creditId
                    );

                    return Optional.of(shardName);
                }

            } catch (Exception e) {
                log.debug("Ошибка при поиске кредита на шарде {}: {}",
                        shardName, e.getMessage());
            }
        }

        log.warn("❌ Кредит ID {} не найден ни на одном шарде", creditId);
        return Optional.empty();
    }


    /**
     * Получить список всех шардов с их DataSource
     */
    public Map<String, DataSource> getShardDataSources() {
        return Collections.unmodifiableMap(dataSources);
    }

    /**
     * Получить DataSource конкретного шарда
     */
    public Optional<DataSource> getShardDataSource(String shardName) {
        return Optional.ofNullable(dataSources.get(shardName));
    }
    /**
     * Универсальный метод для поиска сущности в конкретном шарде по ID
     * Возвращает сущность напрямую
     *
     * @param entityType тип сущности: "client", "credit"
     * @param entityId ID сущности
     * @param shardName имя шарда (shard02, shard03, shard04, shard05)
     * @return Optional с сущностью, если найдена
     */
    public <T> Optional<T> findEntityInSpecificShard(
            String entityType,
            Long entityId,
            String shardName) {

        if (entityType == null || entityType.isEmpty()) {
            log.warn("Тип сущности не может быть пустым");
            return Optional.empty();
        }

        if (entityId == null) {
            log.warn("ID сущности не может быть null");
            return Optional.empty();
        }

        if (shardName == null || shardName.isEmpty()) {
            log.warn("Имя шарда не может быть пустым");
            return Optional.empty();
        }

        log.info("Поиск сущности {} ID {} в шарде: {}", entityType, entityId, shardName);

        RoutingDataSource.setCurrentShard(shardName);

        try {
            Optional<?> entityOpt;

            switch (entityType.toLowerCase()) {
                case "client":
                    entityOpt = clientRepository.findById(entityId);
                    break;

                case "credit":
                    entityOpt = creditRepository.findById(entityId);
                    if (entityOpt.isPresent()) {
                        // Для кредита можно загрузить связанного клиента, если нужно
                        Credit credit = (Credit) entityOpt.get();
                        if (credit.getClient() != null) {
                            // Чтобы избежать LazyLoadingException
                            credit = creditRepository.findByIdWithClient(entityId).orElse(credit);
                        }
                    }
                    break;

                default:
                    log.error("Неизвестный тип сущности: {}", entityType);
                    return Optional.empty();
            }

            if (entityOpt.isPresent()) {
                log.info("✅ Сущность {} ID {} найдена в шарде: {}", entityType, entityId, shardName);
                return (Optional<T>) entityOpt;
            } else {
                log.info("❌ Сущность {} ID {} не найдена в шарде: {}", entityType, entityId, shardName);
                return Optional.empty();
            }

        } catch (Exception e) {
            log.error("Ошибка при поиске сущности {} ID {} в шарде {}: {}",
                    entityType, entityId, shardName, e.getMessage());
            return Optional.empty();
        } finally {
            RoutingDataSource.clearCurrentShard();
        }
    }
}
