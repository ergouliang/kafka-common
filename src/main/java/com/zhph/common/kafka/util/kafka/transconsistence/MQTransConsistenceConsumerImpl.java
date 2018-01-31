package com.zhph.common.kafka.util.kafka.transconsistence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import com.zhph.common.kafka.service.TransMsgConsumerLogService;
import com.zhph.common.kafka.service.mq.transconsistence.IMQConsumerCallback;
import com.zhph.common.kafka.service.mq.transconsistence.IMQConsumerSimpleCallback;
import com.zhph.common.kafka.service.mq.transconsistence.MQTransConsistenceConsumer;
import org.apache.kafka.clients.consumer.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 事务最终一致性组件消费者接口实现类
 *
 * @author Administrator
 *
 */
// @Service("mQTransConsistence")
public class MQTransConsistenceConsumerImpl implements MQTransConsistenceConsumer {

    private static Logger logger = LoggerFactory.getLogger(MQTransConsistenceConsumerImpl.class);
    private MessageConsumerPool messageConsumerPool;
    private MessageProducerPool messageProducerPool;
    private TransMsgConsumerLogService transMsgConsumerLogService;
    private final List<Consumer<Object, Object>> usedConsumers = new ArrayList<Consumer<Object, Object>>();
    private final ScheduledExecutorService executors = Executors.newScheduledThreadPool(60);
    
    /**
     * 得到消费者连接池
     * @return 消费者连接池
     */
    public MessageConsumerPool getMessageConsumerPool() {
        return messageConsumerPool;
    }
    /**
     * 设置消费者连接池
     */
    public void setMessageConsumerPool(MessageConsumerPool messageConsumerPool) {
        this.messageConsumerPool = messageConsumerPool;
    }
    /**
     * 得到生产者连接池
     * @return 生产者连接池
     */
    public MessageProducerPool getMessageProducerPool() {
        return messageProducerPool;
    }
    /**
     * 设置生产者连接池
     */
    public void setMessageProducerPool(MessageProducerPool messageProducerPool) {
        this.messageProducerPool = messageProducerPool;
    }
    
    public TransMsgConsumerLogService getTransMsgConsumerLogService() {
		return transMsgConsumerLogService;
	}
	public void setTransMsgConsumerLogService(TransMsgConsumerLogService transMsgConsumerLogService) {
		this.transMsgConsumerLogService = transMsgConsumerLogService;
	}
	/**
     * 消费消息方法，消费成功后发送自定义返回消息
     * @param topic 被消费的消息主题
     * @param action 消费消息的业务方法接口实现对象
     * @param rate 拉取消息的频率(秒)
     * @param params 传入消费消息的业务方法的自定义参数
     * @throws RuntimeException
     */
    @Override
    public void consumerMessage(String topic, IMQConsumerCallback action, long rate)
            throws RuntimeException {
        this.consumerMessageCommon(topic, action, rate, ConsumerBusinessThread.C_MsgEcho);
    }
    /**
     * 消费消息方法，消费成功后发送通用的确认返回消息
     * @param topic 被消费的消息主题
     * @param action 消费消息的业务方法接口实现对象
     * @param rate 拉取消息的频率(秒)
     * @param params 传入消费消息的业务方法的自定义参数
     * @throws RuntimeException
     */
    @Override
    public void consumerSimpleMessage(String topic, IMQConsumerSimpleCallback action, long rate)
            throws RuntimeException {
        this.consumerMessageCommon(topic, action, rate, ConsumerBusinessThread.C_SimpleEcho);
    }
    /**
     * 消费消息后不发送任何确认消息，通常用于生产者接收到消费者的返回消息后做后续处理
     * @param topic 被消费的消息主题
     * @param action 消费消息的业务方法接口实现对象
     * @param rate 拉取消息的频率(秒)
     * @param params 传入消费消息的业务方法的自定义参数
     * @throws RuntimeException
     */
    @Override
    public void consumerMessageEchoNone(String topic, IMQConsumerSimpleCallback action, long rate)
            throws RuntimeException {
        this.consumerMessageCommon(topic, action, rate, ConsumerBusinessThread.C_NonEcho);
    }
    /**
     * 通用的消费消息方法
     * @param topic 被消费的消息主题
     * @param action 消费消息的业务方法接口实现对象
     * @param rate 拉取消息的频率(毫秒)
     * @param consumerType 消费者类型C_MsgEcho，C_SimpleEcho，C_NonEcho
     * @param params 传入消费消息的业务方法的自定义参数
     * @throws RuntimeException
     */
    private void consumerMessageCommon(final String topic, final Object action, final long rate,
            final String consumerType) throws RuntimeException {
        if (topic == null || "".equals(topic)) {
            logger.error("====================property topic of message is empty !!!");
            throw new RuntimeException("property topic is empty !!!");
        }
        Consumer<Object, Object> consumer = messageConsumerPool.getConsumer();
        if (consumer == null)
            return;
        this.usedConsumers.add(consumer);

        try {
            consumer.subscribe(Arrays.asList(topic));
            ConsumerBusinessThread ct = null;
            if (ConsumerBusinessThread.C_MsgEcho.equals(consumerType))
                ct = new ConsumerBusinessThread(consumer, (IMQConsumerCallback) action, topic,
                        this.messageProducerPool, transMsgConsumerLogService, consumerType);
            else if (ConsumerBusinessThread.C_SimpleEcho.equals(consumerType)
                    || ConsumerBusinessThread.C_NonEcho.equals(consumerType))
                ct = new ConsumerBusinessThread(consumer, (IMQConsumerSimpleCallback) action, topic,
                        this.messageProducerPool, transMsgConsumerLogService, consumerType);

            executors.scheduleWithFixedDelay(ct, 0, rate, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            logger.error("接收消息时出错！消息主题={},错误信息：{}", topic, e.getCause());
            throw new RuntimeException(e);
        }
    }

    /**
     * 交还消费者连接给连接池
     */
    @PreDestroy
    public void returnConsumers() {
        if (this.usedConsumers.size() > 0) {
            for (Consumer<Object, Object> consumer : this.usedConsumers) {
                if (consumer != null)
                    messageConsumerPool.returnConnection(consumer);
            }
        }
        if (executors != null && ! executors.isShutdown() && ! executors.isTerminated())
        	executors.shutdown();
    }
}
