package com.zhph.common.kafka.service.mq.transconsistence;

public interface IMQConsumerMultipleCallback extends IMQConsumerCallbackBase{
	/**
	 * 消息消费者处理多个消息
	 * @param msgs [{msgBody:{},msgId:""}]
	 * @return 处理消息是否成功
	 */
	Boolean doConsumerBusiness(String msgs) throws RuntimeException;
	
}
