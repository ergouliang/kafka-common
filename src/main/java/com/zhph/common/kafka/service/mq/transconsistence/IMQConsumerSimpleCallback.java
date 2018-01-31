package com.zhph.common.kafka.service.mq.transconsistence;

public interface IMQConsumerSimpleCallback {
	/**
	 * 消息消费者处理消息
	 * @param msgBody
	 * @param msgId
	 * @return 处理消息是否成功
	 */
	Boolean doConsumerBusiness(String msgBody, String msgId) throws RuntimeException;
}
