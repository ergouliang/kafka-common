package com.zhph.common.kafka.util.kafka.transconsistence;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.PartitionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zhph.common.kafka.util.SerializeUtil;
import com.zhph.common.kafka.util.kafka.pool.PoolConfig;
import com.zhph.common.kafka.util.kafka.pool.impl.ProducerPool;

public class MessageProducerPool {
		
	private Logger logger = LoggerFactory.getLogger(MessageProducerPool.class);
	private ProducerPool pool;
	private Boolean productPoolSwitch;
	
	public Boolean getProductPoolSwitch() {
		return productPoolSwitch;
	}

	public void setProductPoolSwitch(Boolean productPoolSwitch) {
		this.productPoolSwitch = productPoolSwitch;
	}

	public void init() {
		InputStream in = null;
		Properties props = new Properties();
		PoolConfig config = new PoolConfig();
		try {
			if(this.getProductPoolSwitch()) {
				in = SerializeUtil.class.getClassLoader().getResourceAsStream("kafka-producer.properties");
				props.load(in);
				/* 对象池配置 */
				config.setMaxTotal(Integer.parseInt(props.getProperty("pool.maxTotal","4")));
				config.setMaxIdle(Integer.parseInt(props.getProperty("pool.maxIdle","5")));
				config.setMaxWaitMillis(Integer.parseInt(props.getProperty("pool.maxWaitMillis","1000")));
				config.setTestOnBorrow(Boolean.parseBoolean(props.getProperty("pool.maxWaitMillis","true")));
	
				/* 初始化对象池 */
				pool = new ProducerPool(config, props);
			}
		} catch (IOException e) {
			logger.error("kafka配置文件加载出错！");
		} finally{
			try {
				if (in!=null)
					in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error("kafka配置文件释放出错！");
			}
		}
	}
	
	private MessageProducerPool(){}
	
	public Future<RecordMetadata> send(String topic, String key, String message,Integer partition) throws RuntimeException{
		if(!this.getProductPoolSwitch()) {
			throw new RuntimeException("send msg is failed,because the productPoolSwitch is closed!");
		}
		/* 从对象池获取对象 */
		if (pool==null)
			throw new RuntimeException("kafka producer pool is null !!!");
		Producer<Object, Object> producer = pool.getConnection();
		try {
			return producer.send(new ProducerRecord<Object, Object>(topic,partition, key, message));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally{
			if (producer!=null)
				pool.returnConnection(producer);
		}
	}
	
	/**
	 * 获取topic的partition数量
	 * @param topic
	 * @return
	 */
	public Integer partitionsForTopic(String topic) {
		if(!this.getProductPoolSwitch()) {
			throw new RuntimeException("send msg is failed,because the productPoolSwitch is closed!");
		}
		if (pool==null)
			throw new RuntimeException("kafka producer pool is null !!!");
		Producer<Object, Object> producer = pool.getConnection();
		Integer partiton = 0;
		try {
			List<PartitionInfo> partitions = producer.partitionsFor(topic);
			if(partitions != null) partiton = partitions.size();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally{
			if (producer!=null)
				pool.returnConnection(producer);
		}
		return partiton;
	}
	
	public void close(){
		if (pool!=null)
			pool.close();
	}

//	public static void main(String[] args){
//		MessageProducerPool pool = new MessageProducerPool();
//		try{
//			for(int i=0;i<10;i++)
//				pool.send("test", "1", "Hello Kafka Java API "+i+" !!!");
//		}finally{
//			pool.close();
//		}
//	}
}
