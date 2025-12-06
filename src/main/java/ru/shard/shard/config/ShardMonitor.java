package ru.shard.shard.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.shard.shard.config.RoutingDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class ShardMonitor {

    private final Map<String, DataSource> dataSources;

    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {
        logShardInfo();
        checkAllShards();
    }

    @Scheduled(fixedDelay = 30000)
    public void logShardInfo() {
        int totalShards = dataSources.size();
        int connectedShards = RoutingDataSource.getConnectedShardsCount();

        log.info("=== ИНФОРМАЦИЯ О ШАРДАХ ===");
        log.info("Всего шардов в конфигурации: {}", totalShards);
        log.info("Подключено шардов: {}", connectedShards);
        log.info("Использованные шарды: {}", RoutingDataSource.getUsedShards());
        log.info("Текущий активный шард: {}", RoutingDataSource.getCurrentShard());
        log.info("==========================");
    }

    public void checkAllShards() {
        log.info("Проверка подключения ко всем шардам...");

        dataSources.forEach((shardName, dataSource) -> {
            try (Connection conn = dataSource.getConnection()) {
                JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
                String version = jdbcTemplate.queryForObject("SELECT version()", String.class);

                log.info("✓ Шард '{}' доступен: {}", shardName,
                        version != null ? version.split(",")[0] : "OK");

            } catch (SQLException e) {
                log.error("✗ Шард '{}' недоступен: {}", shardName, e.getMessage());
            }
        });
    }
}
