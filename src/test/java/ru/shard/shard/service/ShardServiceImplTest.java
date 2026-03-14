package ru.shard.shard.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.shard.shard.config.ShardManager;
import ru.shard.shard.exception.CreditNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShardServiceImplTest {

	@Mock
	private ShardManager shardManager;

	@InjectMocks
	private ShardServiceImpl shardService;

	@Test
	void getShardNameByCreditIdReturnsShardWhenFound() {
		when(shardManager.determineShardByCreditId(1L)).thenReturn(Optional.of("shard02"));
		assertThat(shardService.getShardNameByCreditId(1L)).isEqualTo("shard02");
	}

	@Test
	void getShardNameByCreditIdThrowsWhenNotFound() {
		when(shardManager.determineShardByCreditId(999L)).thenReturn(Optional.empty());
		assertThatThrownBy(() -> shardService.getShardNameByCreditId(999L))
				.isInstanceOf(CreditNotFoundException.class)
				.hasMessageContaining("999");
	}

	@Test
	void getAllShardsDelegatesToShardManager() {
		List<String> shards = List.of("shard02", "shard03");
		when(shardManager.getAllShards()).thenReturn(shards);
		assertThat(shardService.getAllShards()).isEqualTo(shards);
	}

	@Test
	void determineShardByCreditIdDelegatesToShardManager() {
		when(shardManager.determineShardByCreditId(1L)).thenReturn(Optional.of("shard03"));
		assertThat(shardService.determineShardByCreditId(1L)).contains("shard03");
	}
}
