package com.zhph.common.kafka.service.transconsistence.listener;

/**
 * 消息消费监听器容器
 *
 */
public interface ConsumerMsgListenerContainer {
    /**
     * 拉取消息
     */
	void pullMsg();
	
	/**
	 * 订阅注册监听服务
	 * @param topic 消息topic
	 * @param listener 监听器实例
	 */
	void subscribe(String topic, ConsumerMsgListenerProxy listener);
	
	/**
	 * 给监听器发布消息
	 */
	void publishMsg(String topic);
}
