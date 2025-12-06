package ru.shard.shard.config;


import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import ru.shard.shard.config.property.ShardProperties;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@EnableConfigurationProperties(ShardProperties.class)
public class DataSourceConfig {

    private final ShardProperties shardProperties;

    public DataSourceConfig(ShardProperties shardProperties) {
        this.shardProperties = shardProperties;
        log.info("DataSourceConfig создан, всего шардов в свойствах: {}",
                shardProperties.getShards().size());
    }

    @Bean
    public Map<String, DataSource> dataSources() {
        log.info("Создаем DataSource для всех шардов...");
        Map<String, DataSource> dataSourceMap = new HashMap<>();

        if (shardProperties.getShards().isEmpty()) {
            log.warn("ВНИМАНИЕ: список шардов пуст! Проверьте application.yml");
        }

        shardProperties.getShards().forEach((shardName, shardConfig) -> {
            log.info("Создаем DataSource для шарда: {}", shardName);

            HikariDataSource dataSource = new HikariDataSource();
            dataSource.setJdbcUrl(shardConfig.getUrl());
            dataSource.setUsername(shardConfig.getUsername());
            dataSource.setPassword(shardConfig.getPassword());
            dataSource.setMinimumIdle(2);
            dataSource.setConnectionTimeout(30000);
            dataSource.setPoolName(shardName + "-pool");

            dataSourceMap.put(shardName, dataSource);

            log.info("DataSource для шарда {} создан: {}", shardName, shardConfig.getUrl());
        });

        log.info("Всего создано DataSource: {}", dataSourceMap.size());
        return dataSourceMap;
    }

    @Bean
    @Primary
    public DataSource routingDataSource(Map<String, DataSource> dataSources) {
        log.info("Создаем RoutingDataSource, всего DataSource: {}", dataSources.size());

        if (dataSources.isEmpty()) {
            log.error("ОШИБКА: нет DataSource для создания RoutingDataSource!");
            throw new IllegalStateException("Нет DataSource для создания RoutingDataSource");
        }

        RoutingDataSource routingDataSource = new RoutingDataSource();

        // Берем первый шард как дефолтный
        String firstShard = dataSources.keySet().iterator().next();
        DataSource defaultDataSource = dataSources.get(firstShard);
        routingDataSource.setDefaultTargetDataSource(defaultDataSource);

        // Устанавливаем все шарды
        Map<Object, Object> targetDataSources = new HashMap<>(dataSources);
        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.afterPropertiesSet();

        log.info("RoutingDataSource создан с {} шардами, дефолтный: {}",
                dataSources.size(), firstShard);

        return routingDataSource;
    }
}
