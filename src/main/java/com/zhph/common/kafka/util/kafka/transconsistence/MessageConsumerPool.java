package com.zhph.common.kafka.util.kafka.transconsistence;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

import com.zhph.common.kafka.util.SerializeUtil;
import com.zhph.common.kafka.util.kafka.pool.impl.ConsumerPool;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zhph.common.kafka.util.kafka.pool.PoolConfig;

public class MessageConsumerPool {
	
	private Logger logger = LoggerFactory.getLogger(MessageConsumerPool.class);
	private ConsumerPool pool;
	
	public MessageConsumerPool(){
		InputStream in = null;
		Properties props = new Properties();
		PoolConfig config = new PoolConfig();
		try {
			in = SerializeUtil.class.getClassLoader().getResourceAsStream("kafka-consumer.properties");
			props.load(in);
		     /* 对象池配置 */
			config.setMaxTotal(Integer.parseInt(props.getProperty("pool.maxTotal","4")));
			config.setMaxIdle(Integer.parseInt(props.getProperty("pool.maxIdle","5")));
			config.setMaxWaitMillis(Integer.parseInt(props.getProperty("pool.maxWaitMillis","1000")));
			config.setTestOnBorrow(Boolean.parseBoolean(props.getProperty("pool.maxWaitMillis","true")));
			/* 初始化对象池 */
			pool = new ConsumerPool(config, props);
		} catch (IOException e) {
			logger.error("kafka配置文件加载出错！");
		} catch (Exception e) {
			logger.error("kafka消费者连接池创建出错：{}",e.getMessage());
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
		
	/**
	 * 拉取消息
	 * @throws RuntimeException
	 */
	public ConsumerRecords<Object, Object> subscribe(String topic) throws RuntimeException{
		/* 从对象池获取对象 */
		if (pool==null)
			throw new RuntimeException("kafka consumer pool is null !!!");
		Consumer<Object, Object> consumer = pool.getConnection();
		
		ConsumerRecords<Object, Object> records = null;
		try {
			consumer.subscribe(Arrays.asList(topic));
			records = consumer.poll(1000);
			
			if (records == null || records.count()==0)
				return null;
			else
				return records;
		} catch (Exception e) {
			logger.error("接收消息时出错！消息主题={},错误信息：{}",topic,e.getCause());
			return null;
		} finally{
			pool.returnConnection(consumer);
		}
		
	}
	
	public Consumer<Object, Object> getConsumer(){
		return pool.getConnection();
	}
	
	public void returnConnection(Consumer<Object, Object> consumer){
		pool.returnConnection(consumer);
	}
	
	public void close(){
		if (pool!=null)
			pool.close();
	}
	
//	public static void main(String[] args){
//		String topic = "test123";
//		MessageConsumerPool consumer = new MessageConsumerPool();
//		try {
//		     ConsumerRecords<Object, Object> records = consumer.subscribe(topic);
//		     if (records!=null && records.count()>0){
//		    	 ConsumerRecord<Object, Object> record = records.iterator().next();
//
//		    	 System.out.printf ("offset = %d, key = %s, value = %s\r\n", record.offset(), ((Long)record.key()).longValue(), record.value());
//		     }
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new RuntimeException(e);
//		}finally{
//			consumer.close();
//		}
//	}
}
