package com.zhph.common.kafka.util.kafka.transconsistence;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import com.zhph.common.kafka.service.TransMsgLogService;
import com.zhph.common.kafka.service.mq.transconsistence.IMQConsumerSimpleCallback;
import com.zhph.common.kafka.util.GlobalUtil;
import com.zhph.common.kafka.util.StringUtil;
import org.apache.kafka.clients.consumer.Consumer;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.zhph.common.kafka.constants.Constants;
import com.zhph.common.kafka.model.TransMsgLog;
import com.zhph.common.kafka.service.mq.transconsistence.MQTransConsistenceConsumer;
import com.zhph.common.kafka.service.mq.transconsistence.MQTransConsistenceProducer;
import com.zhph.common.kafka.util.log4j.ZhphLogger;


/**
 * 事务最终一致性消息生产者接口实现类
 *
 */
// @Service("mQTransConsistence")
public class MQTransConsistenceProducerImpl implements MQTransConsistenceProducer {

    private TransMsgLogService transMsgLogService;
    private MQTransConsistenceConsumer mQTransConsistenceConsumer;
    private MessageProducerPool messageProducerPool;
    private int updateMsgLogRate;
    private String producerPrefix;
    private final List<Consumer<Object, Object>> usedConsumers = new ArrayList<Consumer<Object, Object>>();
    private final int RETRY_LIMIT = 3;

    public int getUpdateMsgLogRate() {
        return updateMsgLogRate;
    }

    public void setUpdateMsgLogRate(int updateMsgLogRate) {
        this.updateMsgLogRate = updateMsgLogRate;
    }

    public TransMsgLogService getTransMsgLogService() {
        return transMsgLogService;
    }

    public void setTransMsgLogService(TransMsgLogService transMsgLogService) {
        this.transMsgLogService = transMsgLogService;
    }

    public MessageProducerPool getMessageProducerPool() {
        return messageProducerPool;
    }

    public void setMessageProducerPool(MessageProducerPool messageProducerPool) {
        this.messageProducerPool = messageProducerPool;
    }

    /**
     * @return the mQTransConsistenceConsumer
     */
    public MQTransConsistenceConsumer getmQTransConsistenceConsumer() {
        return mQTransConsistenceConsumer;
    }

    /**
     * @param mQTransConsistenceConsumer
     *            the mQTransConsistenceConsumer to set
     */
    public void setmQTransConsistenceConsumer(MQTransConsistenceConsumer mQTransConsistenceConsumer) {
        this.mQTransConsistenceConsumer = mQTransConsistenceConsumer;
    }

    /**
     * @return the producerPrefix
     */
    public String getProducerPrefix() {
        return producerPrefix;
    }

    /**
     * @param producerPrefix
     *            the producerPrefix to set
     */
    public void setProducerPrefix(String producerPrefix) {
        this.producerPrefix = producerPrefix;
    }

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
     *            重试最大次数，传0自动设置成6次
     * @param createdId
     *            当前操作用户ID
     * @throws RuntimeException
     */
    @Override
    public void producerSaveAndSendMsgLog(final String topic, final String msgBody, final String busiNo,
            final String callbackTopic, final int retryLimit, final String createdId) throws RuntimeException {
    	
        TransMsgLog msgLog = this.createInfoOfMsgLog(topic, msgBody, busiNo, callbackTopic, retryLimit, createdId);
        if (msgLog == null) {
            throw new RuntimeException("消息数据不完整");
        }

        // 记录日志
        try {
            transMsgLogService.saveMsgLog(msgLog);
        } catch (Exception e1) {
            ZhphLogger.error("消息生产者发送消息时出错，消息主题：{}, 消息体：{}, 错误信息：{}", topic, msgBody, e1.getMessage());
            throw new RuntimeException(e1);
        }

        // 向消息队列发消息
        try {
            messageProducerPool.send(msgLog.getMsgName(), msgLog.getId(), msgLog.getMsgBody());
        } catch (Exception e) {
            ZhphLogger.error("生产者向消息队列发送消息出错！消息ID={},消息主题={},错误信息：{}", msgLog.getId(), msgLog.getMsgName(), e.getCause());
            throw new RuntimeException(e);
        }
    }

    @Override
	public String producerSaveAndSendMsgLogNeedReturn(String topic, String msgBody, String busiNo, String callbackTopic,
			int retryLimit, String createdId) {
    	try {
			this.producerSaveAndSendMsgLog(topic, msgBody, busiNo, callbackTopic, retryLimit, createdId);
		} catch (Exception e) {
			return "-1";
		}
		return busiNo;
	}

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
    @Override
    public void producerSaveAndSendMultiMsgLog(final List<TransMsgLog> msgLog) throws RuntimeException {
        if (msgLog == null || msgLog.size() == 0) {
            throw new RuntimeException("消息数据为空");
        }
        for (TransMsgLog ml : msgLog) {
            if (!this.fillInfoOfMsgLog(ml)) {
                throw new RuntimeException("消息数据不完整");
            }
        }

        for (TransMsgLog ml : msgLog) {
            // 记录日志
            try {
                transMsgLogService.saveMsgLog(ml);
            } catch (Exception e1) {
                ZhphLogger.error("消息生产者发送消息时出错，消息主题：{}, 消息体：{}, 错误信息：{}", ml.getMsgName(), ml.getMsgBody(),
                        e1.getMessage());
                throw new RuntimeException(e1);
            }

            // 向消息队列发消息
            try {
                messageProducerPool.send(ml.getMsgName(), ml.getId(), ml.getMsgBody());
            } catch (Exception e) {
                ZhphLogger.error("生产者向消息队列发送消息出错！消息ID={},消息主题={},错误信息：{}", ml.getId(), ml.getMsgName(), e.getCause());
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 创建msgLog对象
     *
     * @param topic
     *            （消息topic，如user.addMember）不能为空
     * @param msgBody
     *            传递给消费者的参数，以Json字符串表示,不能为空
     * @param busiNo
     *            业务流水号或对象ID，为空则默认存topic
     * @param callbackTopic
     *            消息消费者回复消费成功的消息topic：如果需要接收消费者的自定义返回消息就定义一个接收返回消息的topic，否则传null
     * @param retryLimit
     *            消息消费者回复消费成功的消息topic：如果需要接收消费者的自定义返回消息就定义一个接收返回消息的topic，否则传null
     * @param createdId
     *            当前操作用户ID
     * @return TransMsgLog成功，null信息不完整
     * @throws RuntimeException
     *             生产者前缀属性producerPrefix没有配置
     */
    private TransMsgLog createInfoOfMsgLog(String topic, String msgBody, String busiNo, 
    		String callbackTopic, int retryLimit, String createdId)
            throws RuntimeException {
        if (topic == null || "".equals(topic) || msgBody == null || "".equals(msgBody)) {
            return null;
        }
        String updateStatusTopic = this.producerPrefix + "." + Constants.TRANS_REPLY_TOPIC;
        TransMsgLog msgLog = new TransMsgLog();
        msgLog.setMsgName(topic);
        if (! StringUtil.isEmptyOrNull(busiNo))
            msgLog.setMsgPublisher(busiNo);
        msgLog.setPublishTime(new Date());
        msgLog.setId(GlobalUtil.createGlobalId());
//        msgLog.setId(System.currentTimeMillis()+"");
        if (callbackTopic == null || "".equals(callbackTopic)) {
            if (this.producerPrefix != null && !"".equals(this.producerPrefix))
                callbackTopic = updateStatusTopic;
            else
                throw new RuntimeException("生产者前缀属性producerPrefix没有配置！");
        }else{
        	callbackTopic += "|"+updateStatusTopic;
        }
        msgLog.setCallbackTopicName(callbackTopic);
        msgLog.setMsgType("0");
        msgLog.setRetryCount(1);
        msgLog.setRetryLimit(retryLimit > 0 ? retryLimit : this.RETRY_LIMIT);
        msgLog.setStatus("1");
        msgLog.setCreatedId(createdId);
        String body = this.packageMsgBody(msgBody, msgLog.getCallbackTopicName(), msgLog.getRetryLimit(), msgLog.getMsgPublisher(), msgLog.getCreatedId());
        msgLog.setMsgBody(body);
        return msgLog;
    }

    /**
     * 补充msgLog信息
     *
     * @param msgLog
     *            TransMsgLog对象，msgName, msgBody不能为空
     * @return true成功 false信息不完整
     */
    private boolean fillInfoOfMsgLog(final TransMsgLog msgLog) throws RuntimeException {
        if (msgLog == null || msgLog.getMsgName() == null || "".equals(msgLog.getMsgName())
                || msgLog.getMsgBody() == null || "".equals(msgLog.getMsgBody())) {
            return false;
        }
        
//        msgLog.setId(System.currentTimeMillis()+"");
        msgLog.setId(GlobalUtil.createGlobalId());
        if (msgLog.getPublishTime() == null)
            msgLog.setPublishTime(new Date());
        msgLog.setMsgType("0");
        msgLog.setRetryCount(1);
        msgLog.setRetryLimit((msgLog.getRetryLimit() != null && msgLog.getRetryLimit() > 0) ? msgLog.getRetryLimit() : this.RETRY_LIMIT);
        msgLog.setStatus("1");
        String callbackTopic = msgLog.getCallbackTopicName();
        if (callbackTopic == null || "".equals(callbackTopic)) {
            if (this.producerPrefix != null && !"".equals(this.producerPrefix))
                callbackTopic = this.producerPrefix + "." + Constants.TRANS_REPLY_TOPIC;
            else
                throw new RuntimeException("生产者前缀属性producerPrefix没有配置！");
        }
        msgLog.setCallbackTopicName(callbackTopic);
        String body = this.packageMsgBody(msgLog.getMsgBody(), callbackTopic, msgLog.getRetryLimit(),
        		msgLog.getMsgPublisher(), msgLog.getCreatedId());
        msgLog.setMsgBody(body);
        
        return true;
    }

    /**
     * 重新包装msgBody，将callbackTopic和原始msgBody封装成新的Json字符串
     * 
     * @param msgBody
     *            原始消息体
     * @param callbackTopic
     *            消息消费者确认消费成功的消息主题
     * @return 重新包装后的msgBody
     */
    private String packageMsgBody(final String msgBody, final String callbackTopic, final int retryLimit,
    		final String msgPublisher, final String createdId) {
    	TransMsgLog log = new TransMsgLog();
    	log.setMsgBody(msgBody);
    	log.setCallbackTopicName(callbackTopic);
    	log.setRetryLimit(retryLimit > 0 ? retryLimit : this.RETRY_LIMIT);
    	log.setMsgPublisher(msgPublisher);
    	log.setCreatedId(createdId);
    	
        return JSON.toJSONString(log);
    }

    /**
     * 生产者处理消费者发来的确认消息
     *
     * @param topic
     *            确认消息的topic
     * @param action
     *            处理消费者发来的确认消息的业务方法接口对象
     * @param rate
     *            拉取消息的频率(秒)
     */
    @Override
    public void producerDealAfterGetReplyMessage(final String topic, final IMQConsumerSimpleCallback action, long rate) throws RuntimeException {
        mQTransConsistenceConsumer.consumerMessageEchoNone(topic, new IMQConsumerSimpleCallback() {
            @Override
            public Boolean doConsumerBusiness(String msgBody, String msgId) throws RuntimeException {
                return dealAfterEcoh(action, msgBody, msgId, topic);
            }
        }, rate);
    }

    /**
     * 消费者返回自定义的确认消息后，生产者做相应业务处理，并设置消息表状态为已消费
     * 
     * @param action
     *            处理消费者发来的自定义确认消息的业务方法接口对象
     * @param msgBody
     *            消息体
     * @param msgId
     *            消息ID
     * @param replyTopic
     *            确认消息的消息主题
     * @param params
     *            传入业务方法接口对象的参数
     * @return 处理消息是否成功
     */
    private Boolean dealAfterEcoh(IMQConsumerSimpleCallback action, String msgBody, String msgId, String replyTopic,
            Object... params) {
        try {
            Boolean result = action.doConsumerBusiness(msgBody, msgId);
            if (result != null && result) {
                // 消费者消费消息，成功后返回消息ID
                this.transMsgLogService.updateMsgLogStatus(msgId);
                return true;
            } else {
                ZhphLogger.error("====================生产者接收到消费者发来的确认消息后，进行后续业务处理失败，消息主题={},消息ID={}", replyTopic, msgId);
            }
        } catch (Exception e) {
            ZhphLogger.error("====================生产者接收到消费者发来的确认消息后，进行后续业务处理失败，消息主题={},消息ID={}", replyTopic, msgId);
        }

        return false;
    }

    /**
     * 轮训检查没有处理成功的消息日志，重新发送消息到消息队列
     */
    public void checkMsgLogAndSendMsg() {
        // 得到待发送或待回滚的msgLog
        List<TransMsgLog> logs = transMsgLogService.selectMsgOfNeedSend("0");
        if (logs == null || logs.size() == 0)
            return;

        for (TransMsgLog log : logs) {
        	if (log.getRetryCount().intValue() >= log.getRetryLimit().intValue())
        		continue;
            long nh = 1000 * 60 * 60;
            Long hour = 0L;
            Long subVal = 0L;
            try {
	            //更新时间在一个小时内的推迟重试
	        	if(!StringUtils.isEmpty(log.getUpdateTime())) {
	        		subVal = new Date().getTime() - log.getUpdateTime().getTime();
	        		hour = subVal/ nh;
	        		if(hour.intValue() < 1) continue;
	        	}
	        	//推送时间在一个小时内推迟重试
	        	if(!StringUtils.isEmpty(log.getPublishTime())) {
	        		subVal = new Date().getTime() - log.getPublishTime().getTime();
	        		hour = subVal / nh;
	        		if(hour.intValue() < 1) continue;
	        	}
                sendMsg(log);
            } catch (RuntimeException e) {
                e.printStackTrace();
                ZhphLogger.error("发送消息出错：msgLogId={},错误信息：{}", log.getId(), e.getMessage());
            }
            // 更新消息重试次数
            Integer c = log.getRetryCount();
            if (c == null || c.equals(0))
                c = 1;
            else
                c = c + 1;
            log.setRetryCount(c);
            log.setUpdateTime(new Date());
            try {
                transMsgLogService.updateMsgLog(log);
            } catch (RuntimeException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                ZhphLogger.error("更新消息重试次数失败：msgLogId={},错误信息：{}", log.getId(), e.getMessage());
            }
        }
    }

    /**
     * 发送消息
     * 
     * @param msgLog
     *            消息对象
     * @throws RuntimeException
     */
    private void sendMsg(TransMsgLog msgLog) throws RuntimeException {
        // 向消息队列发消息
        messageProducerPool.send(msgLog.getMsgName(), msgLog.getId(), msgLog.getMsgBody());
        ZhphLogger.info("send message for msgId={} and msgname={}", msgLog.getId(), msgLog.getMsgName());
    }

    /**
     * 生产者接收确认消息的代理方法：接收到消费者发来的确认消息后，设置消息表状态为已处理
     *
     * @throws Throwable
     */
    @PostConstruct
    public void updateMsgLogStatusAfterGetReplyMessage() throws RuntimeException {
        String replyTopic = this.producerPrefix + "." + Constants.TRANS_REPLY_TOPIC;

        mQTransConsistenceConsumer.consumerMessageEchoNone(replyTopic, new IMQConsumerSimpleCallback() {
            @Override
            public Boolean doConsumerBusiness(final String msgBody, final String msgId)
                    throws RuntimeException {
                return updateMsgLogStatus(msgBody);
            }
        }, updateMsgLogRate);
    }
    
    /**
     * 更新消息状态为已消费
     * 
     * @param msgBody TransMsgLog的Json串
     *            
     * @return 更新是否成功
     */
    private Boolean updateMsgLogStatus(final String msgBody) {
        if (msgBody == null || "".equals(msgBody))
            return false;
        
        try {
        	TransMsgLog log = JSON.parseObject(msgBody, TransMsgLog.class);
        	if (log == null || StringUtil.isEmptyOrNull(log.getId()))
        		throw new RuntimeException("得到的消息体不规范");
            this.transMsgLogService.updateMsgLog(log);
            return true;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            ZhphLogger.error("设置消息表状态时出错，消息ID={},错误信息：{}", msgBody, e.getMessage());
        } catch (RuntimeException e) {
            e.printStackTrace();
            ZhphLogger.error("设置消息表状态时出错，消息ID={},错误信息：{}", msgBody, e.getMessage());
        }

        return false;
    }
}
