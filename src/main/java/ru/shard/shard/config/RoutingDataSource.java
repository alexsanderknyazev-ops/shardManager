package ru.shard.shard.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.lang.Nullable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class RoutingDataSource extends AbstractRoutingDataSource {

    private static final ThreadLocal<String> CURRENT_SHARD = new ThreadLocal<>();

    private static final Set<String> USED_SHARDS = new HashSet<>();

    public static void setCurrentShard(String shard) {
        CURRENT_SHARD.set(shard);
        USED_SHARDS.add(shard);
        log.debug("Установлен шард: {}", shard);
    }

    public static String getCurrentShard() {
        return CURRENT_SHARD.get();
    }

    public static void clearCurrentShard() {
        CURRENT_SHARD.remove();
    }

    public static Set<String> getUsedShards() {
        return new HashSet<>(USED_SHARDS);
    }

    public static int getConnectedShardsCount() {
        return USED_SHARDS.size();
    }

    @Nullable
    @Override
    protected Object determineCurrentLookupKey() {
        String shard = CURRENT_SHARD.get();

        if (shard == null) {
            shard = "shard02";
            CURRENT_SHARD.set(shard);
            USED_SHARDS.add(shard);
            log.info("Шард не установлен, используем шард по умолчанию: {}", shard);
        }

        return shard;
    }

    @Override
    public Connection getConnection() throws SQLException {
        String shard = getCurrentShard();
        log.debug("Получаем соединение с шардом: {}", shard);

        try {
            Connection connection = super.getConnection();
            log.debug("Соединение с шардом {} установлено", shard);
            return connection;
        } catch (SQLException e) {
            log.error("Ошибка подключения к шарду {}: {}", shard, e.getMessage());
            throw e;
        }
    }
}
