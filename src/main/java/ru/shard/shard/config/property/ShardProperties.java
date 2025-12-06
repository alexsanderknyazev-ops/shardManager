package ru.shard.shard.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "sharding")
public class ShardProperties {

    private Map<String, ShardConfig> shards = new HashMap<>();

    @Data
    public static class ShardConfig {
        private String url;
        private String username;
        private String password;
        private String driverClassName;
        private HikariConfig hikari = new HikariConfig();
    }

    @Data
    public static class HikariConfig {
        private int maximumPoolSize = 10;
        private int minimumIdle = 2;
        private long connectionTimeout = 30000;
    }
}
