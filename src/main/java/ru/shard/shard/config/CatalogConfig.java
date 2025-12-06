package ru.shard.shard.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class CatalogConfig {

    @Bean("catalogDataSource")
    @ConfigurationProperties(prefix = "sharding.catalog")
    public DataSource catalogDataSource() {
        return new HikariDataSource();
    }

    @Bean
    public JdbcTemplate catalogJdbcTemplate(@Qualifier("catalogDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
