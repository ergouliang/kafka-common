package com.zhph.common.kafka.service.mq.transconsistence;

import java.util.List;

import com.zhph.common.kafka.model.TransMsgLog;

public interface MQTransConsistenceProducer {
    /**
     * 消息生产者保存单条日志，并发送消息
     *
     * @param topic
     *            （消息topic，如user.addMember）不能为空
     * @param msgBody
     *            传递给消费者的参数，以Json字符串表示,不能为空
     * @param busiNo
     *            业务流水号或对象ID，可以为空
     * @param callbackTopic
     *            消息消费者回复消费成功的消息topic：如果需要接收消费者的自定义返回消息就定义一个接收返回消息的topic，否则传null
     * @param retryLimit
     *            重试最大次数，传0自动设置成6次，重试超过最大次数还未成功，不再重复消费
     * @param createdId
     *            当前操作用户ID
     * @param partitionNo 分区编号，多分区情况下为保证同一业务数据有序，需要指定相同的partitionNo
     * @throws RuntimeException
     */
	void producerSaveAndSendMsgLog(String topic, String msgBody, String busiNo,
            String callbackTopic, int retryLimit, String createdId,String partitionNo) throws RuntimeException;
	
	/**
     * 消息生产者保存单条日志，并发送消息(带返回值 成功返回 ：msgPublisher，失败返回 -1)
     *
     * @param topic
     *            （消息topic，如user.addMember）不能为空
     * @param msgBody
     *            传递给消费者的参数，以Json字符串表示,不能为空
     * @param busiNo
     *            业务流水号或对象ID，可以为空
     * @param callbackTopic
     *            消息消费者回复消费成功的消息topic：如果需要接收消费者的自定义返回消息就定义一个接收返回消息的topic，否则传null
     * @param retryLimit
     *            重试最大次数，传0自动设置成6次，重试超过最大次数还未成功，不再重复消费
     * @param createdId
     *            当前操作用户ID
     * @param partitionNo 分区编号，多分区情况下为保证同一业务数据有序，需要指定相同的partitionNo
     * @throws RuntimeException
     */
	String producerSaveAndSendMsgLogNeedReturn(String topic, String msgBody, String busiNo,
            String callbackTopic, int retryLimit, String createdId,String partitionNo);
	
	
	/**
     * 消费者消费消息，处理带自定义返回值的消费结果，并回执成功消息
     *
     * @param msgLog
     *            id（消息唯一ID）可以为空, 
     *            msgName（消息topic，如user.addMember）不能为空,
     *            msgBody(传递给消费者的参数，以Json字符串表示)不能为空,
     *            msgPublisher(业务ID)不能为空，
     *            createdId(当前用户ID)不能为空，
     *            retryLimit重试最大次数，传0自动设置成6次， 其他可以为空 
     *            其中callbackTopic：
     *            消息消费者回复消费成功的消息topic：如果需要接收消费者的自定义返回消息就定义一个接收返回消息的topic，否则传null
     * @throws RuntimeException
     */
	void producerSaveAndSendMultiMsgLog(final List<TransMsgLog> msgLog) throws RuntimeException;
	
	/**
     * 生产者接收确认消息的模板方法，接收到消费者发来的确认消息后：
     * 1、后续业务处理
     * 2、设置消息表状态为已处理
	 * @param topic 生产者发送的消息主题
	 * @param action 后续业务处理回调接口实现
     * @param rate 拉取消息的频率(豪秒)
     * @return
     * @throws Throwable
     */
    void producerDealAfterGetReplyMessage(String topic, IMQConsumerSimpleCallback action, long rate) throws RuntimeException;
    
    /**
	 * 轮训检查没有处理成功的消息日志，重新发送消息到消息队列
	 */
	void checkMsgLogAndSendMsg();
	
	/**
     * 生产者接收确认消息(没有自定义返回值的消息)的代理方法：接收到消费者发来的确认消息后，设置消息表状态为已处理
     * @return
     * @throws RuntimeException
     */
    void updateMsgLogStatusAfterGetReplyMessage() throws RuntimeException;
}
