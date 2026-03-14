package ru.shard.shard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ShardApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShardApplication.class, args);
	}

}
