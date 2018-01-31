package com.zhph.common.kafka.service.mq.transconsistence;

public interface MQTransConsistenceConsumer {
	
	/**
	 * 消费者消费消息，处理带自定义返回值的消费结果，并回执成功消息
	 * @param topic 消息主题
	 * @param action 后续业务处理回调接口实现
     * @param rate 拉取消息的频率(毫秒)
	 * @throws RuntimeException
	 */
	void consumerMessage(String topic, IMQConsumerCallback action, long rate) throws RuntimeException;
	
	/**
	 * 消费者消费消息，处理后没有返回值，并回执成功消息
	 * @param topic 消息主题
	 * @param action 后续业务处理回调接口实现
     * @param rate 拉取消息的频率(毫秒)
	 * @throws RuntimeException
	 */
	void consumerSimpleMessage(String topic, IMQConsumerSimpleCallback action, long rate) throws RuntimeException;
	
	/**
     * 消费消息后不发送任何确认消息，通常用于生产者接收到消费者的返回消息后做后续处理
     * @param topic 被消费的消息主题
     * @param action 消费消息的业务方法接口实现对象
     * @param rate 拉取消息的频率(毫秒)
     * @throws RuntimeException
     */
    void consumerMessageEchoNone(String topic, IMQConsumerSimpleCallback action, long rate)
            throws RuntimeException;
}
