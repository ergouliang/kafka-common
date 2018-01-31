package com.zhph.common.kafka.service.transconsistence.listener;

import org.osgi.framework.ServiceException;

/**
 * 消息消费监听器代理接口
 *
 */
public interface ConsumerMsgListenerProxy {
	/**
	 * 消费消息
	 * @param msgId 消息ID
	 * @param msgBody 消息体
	 * @return 消费结果
	 */
    Object consumer(String msgId, String msgBody) throws ServiceException;
}
