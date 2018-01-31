package com.zhph.common.kafka.util.kafka.pool.impl;

import java.util.Properties;

import org.apache.kafka.clients.consumer.Consumer;

import com.zhph.common.kafka.util.kafka.pool.ConnectionPool;
import com.zhph.common.kafka.util.kafka.pool.PoolBase;
import com.zhph.common.kafka.util.kafka.pool.PoolConfig;

/**
 * Kafka连接池
 *
 * @author wanggang
 *
 */
public class ConsumerPool extends PoolBase<Consumer<Object, Object>> implements
		ConnectionPool<Consumer<Object, Object>> {

	private static final long serialVersionUID = -1506435964498488591L;

	private static final String LOCAL_MODE = "192.168.7.204:9092,192.168.7.204:9093,192.168.7.204:9094";

	/**
	 * 默认构造方法
	 */
	public ConsumerPool() {
		this(LOCAL_MODE);
	}

	public ConsumerPool(final String brokers) {
		this(new PoolConfig(), brokers);
	}

	public ConsumerPool(final PoolConfig poolConfig, final String brokers) {
		this(poolConfig, brokers, "test");
	}

	public ConsumerPool(final PoolConfig poolConfig, final String brokers, final String groupId) {
		this(poolConfig, brokers, groupId, Boolean.TRUE,
				"org.apache.kafka.common.serialization.StringDeserializer", 30_000);
	}

	public ConsumerPool(final PoolConfig poolConfig, final Properties props) {
		super(poolConfig, new ConsumerFactory(props));
	}

	public ConsumerPool(final PoolConfig poolConfig, final String brokers, final String groupId,
			final boolean autoCommit, final String codec, final long sessionTimeoutMs) {
		super(poolConfig,
				new ConsumerFactory(brokers, groupId, autoCommit, codec, sessionTimeoutMs));
	}

	@Override
	public Consumer<Object, Object> getConnection() {
		return super.getResource();
	}

	@Override
	public void returnConnection(Consumer<Object, Object> conn) {
		super.returnResource(conn);
	}

	@Override
	public void invalidateConnection(Consumer<Object, Object> conn) {
		super.invalidateResource(conn);
	}

}
