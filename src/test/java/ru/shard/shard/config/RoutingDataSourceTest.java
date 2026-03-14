package ru.shard.shard.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RoutingDataSourceTest {

	@AfterEach
	void tearDown() {
		RoutingDataSource.clearCurrentShard();
	}

	@Test
	void getCurrentShardReturnsNullWhenNotSet() {
		RoutingDataSource.clearCurrentShard();
		assertThat(RoutingDataSource.getCurrentShard()).isNull();
	}

	@Test
	void setCurrentShardAndGet() {
		RoutingDataSource.setCurrentShard("shard02");
		assertThat(RoutingDataSource.getCurrentShard()).isEqualTo("shard02");
	}

	@Test
	void clearCurrentShardRemovesValue() {
		RoutingDataSource.setCurrentShard("shard03");
		RoutingDataSource.clearCurrentShard();
		assertThat(RoutingDataSource.getCurrentShard()).isNull();
	}

	@Test
	void getUsedShardsContainsSetShard() {
		RoutingDataSource.clearCurrentShard();
		RoutingDataSource.setCurrentShard("shard04");
		assertThat(RoutingDataSource.getUsedShards()).contains("shard04");
	}

	@Test
	void getConnectedShardsCountIncrementsAfterSet() {
		RoutingDataSource.clearCurrentShard();
		int before = RoutingDataSource.getConnectedShardsCount();
		RoutingDataSource.setCurrentShard("shard05");
		assertThat(RoutingDataSource.getConnectedShardsCount()).isEqualTo(before + 1);
	}
}
