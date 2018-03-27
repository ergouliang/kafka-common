package com.zhph.common.kafka.service.mq.transconsistence;

import java.util.Map;

public interface IMQConsumerCallback extends IMQConsumerCallbackBase{
	/**
	 * 消息消费者处理消息，并返回结果
	 * @param msgBody
	 * @param msgId
	 * @return 返回结果键值对
	 */
	Map<String,Object> doConsumerBusiness(String msgBody, String msgId) throws RuntimeException;
}
