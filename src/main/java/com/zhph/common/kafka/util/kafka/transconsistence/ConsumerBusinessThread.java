package com.zhph.common.kafka.util.kafka.transconsistence;

import java.util.Date;
import java.util.Map;

import com.zhph.common.kafka.exception.MsgStopRetryException;
import com.zhph.common.kafka.model.TransMsgConsumerLog;
import com.zhph.common.kafka.service.TransMsgConsumerLogService;
import com.zhph.common.kafka.service.mq.transconsistence.IMQConsumerCallback;
import com.zhph.common.kafka.service.mq.transconsistence.IMQConsumerSimpleCallback;
import com.zhph.common.kafka.util.StringUtil;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import com.alibaba.fastjson.JSON;
import com.zhph.common.kafka.model.TransMsgLog;
import com.zhph.common.kafka.util.log4j.ZhphLogger;


/**
 * 接收消息并消费消息的线程任务
 * @author Administrator
 *
 */
public class ConsumerBusinessThread extends Thread {

    private final Consumer<Object, Object> consumer;
    private final IMQConsumerSimpleCallback simpleAction;
    private final IMQConsumerCallback action;
    private final String topic;
    private final MessageProducerPool messageProducerPool;
    private final TransMsgConsumerLogService transMsgConsumerLogService;
    // 消费消息类型：C_SimpleEcho, C_MsgEcho, C_NonEcho
    private final String consumerType;
    // 消费消息成功后发送确认消息，消息体为消息ID
    public static final String C_SimpleEcho = "C_SimpleEcho";
    // 消费消息成功后发送确认消息，消息体为具体数据，供生产者处理
    public static final String C_MsgEcho = "C_MsgEcho";
    // 消费消息成功后不发送确认消息，如生产者消费MsgReply.SUCCESS，更改日志状态
    public static final String C_NonEcho = "C_NonEcho";
    private boolean debug = false;

    public ConsumerBusinessThread(Consumer<Object, Object> vconsumer, IMQConsumerSimpleCallback vaction,
            String vtopic, MessageProducerPool vmessageProducerPool, TransMsgConsumerLogService vTransMsgConsumerLogService, 
            String vconsumerType) {
        if (vconsumer == null || vaction == null || vtopic == null || "".equals(vtopic)
                || vconsumerType == null || "".equals(vconsumerType))
            throw new RuntimeException("所有参数都不能为空！");

        this.consumer = vconsumer;
        
        this.simpleAction = vaction;
        this.action = null;
        this.topic = vtopic;
        this.messageProducerPool = vmessageProducerPool;
        this.transMsgConsumerLogService = vTransMsgConsumerLogService;
        this.consumerType = vconsumerType;
        super.setName("Thread-"+vtopic);
    }

    public ConsumerBusinessThread(Consumer<Object, Object> vconsumer, IMQConsumerCallback vaction,
            String vtopic, MessageProducerPool vmessageProducerPool,TransMsgConsumerLogService vTransMsgConsumerLogService,
            String vconsumerType) {
        if (vconsumer == null || vaction == null || vtopic == null || "".equals(vtopic)
                || vconsumerType == null || "".equals(vconsumerType))
            throw new RuntimeException("所有参数都不能为空！");

        this.consumer = vconsumer;
        this.action = vaction;
        this.simpleAction = null;
        this.topic = vtopic;
        this.messageProducerPool = vmessageProducerPool;
        this.transMsgConsumerLogService = vTransMsgConsumerLogService;
        this.consumerType = vconsumerType;
        super.setName("Thread-"+vtopic);
    }

    @Override
    public void run() {
    	try {
    		//Set<String> topics = consumer.subscription();
    		//debug = (topics != null && topics.size()>0 && topics.contains("sc.replication.basic.addProductParam"));
    		if (debug)
    			ZhphLogger.debug("========================================当前Consumer为{}，当前线程ID为{}，准备poll消息，当前时间：{}",consumer.hashCode(),Thread.currentThread().getId(),
    					System.currentTimeMillis());
	        ConsumerRecords<Object, Object> records = consumer.poll(100);
	        if (debug)
	        	ZhphLogger.debug("========================================当前Consumer为{}，当前线程ID为{}，poll消息完毕，当前时间：{}",consumer.hashCode(),Thread.currentThread().getId(),
    					System.currentTimeMillis());
	        if (records != null && records.count() > 0) {
	        	if (debug)
	        		ZhphLogger.debug("========================================当前Consumer为{}，当前线程ID为{}，poll消息成功，当前时间：{}",consumer.hashCode(),Thread.currentThread().getId(),
	    					System.currentTimeMillis());
				if (this.C_SimpleEcho.equals(this.consumerType) ||
						this.C_MsgEcho.equals(this.consumerType))
				    this.consumerBusiness(records, action);
				else if (this.C_NonEcho.equals(this.consumerType))
				    this.consumerBusinessNonEcho(records, simpleAction);
	        }
        } catch (Throwable e) {
        	e.printStackTrace();
			ZhphLogger.error("消息处理出错，错误信息：{}", getStackInfo(e));
		}
    }

    /**
     * 消费消息成功后发送确认消息，消息体为具体数据，供生产者处理
     *
     * @param records
     *            消息记录
     * @param action
     *            消费消息的业务接口
     * @throws RuntimeException
     */
    private void consumerBusiness(ConsumerRecords<Object, Object> records, Object action) {
        if (records == null || records.count() == 0)
            return;

        for (ConsumerRecord<Object, Object> record : records) {
        	// 消费者消费消息，成功后返回消息ID
            //消息体解包
            TransMsgLog log = this.decodeMsgBody(record);
            if (log == null) continue;
            log.setId(record.key() == null ? "" : record.key().toString());
            
            Boolean success = false;
            Map<String, Object> result = null;
            String callbackBody = "{id:\""+log.getId()+"\",status:\"0\"}";
        	String[] callbackTopics = null;
        	if (! StringUtil.isEmptyOrNull(log.getCallbackTopicName())){
        		callbackTopics = log.getCallbackTopicName().split("\\|");
        	}else{
        		ZhphLogger.error("!!!!!!!!!!!!!!!!!!!日志消息中callbackTopic为空");
        		continue;
        	}
        	
            TransMsgConsumerLog consumerLog = null;
			try {
				consumerLog = this.transMsgConsumerLogService.getByPrimaryKey(record.key().toString());
			} catch (Exception e1) {
				e1.printStackTrace();
				ZhphLogger.error("====================消费者处理消息时查询日志表出错！消息主题={},消息ID={},错误信息：{}", record.topic(), record.key(),
						getStackInfo(e1));
				continue;
			}
            
            if (! cancalRetryConsumer(consumerLog)){ //允许重试
            	String errorMsg = null;
            	String status = "1";
	            try {
	            	if (this.C_SimpleEcho.equals(this.consumerType)){
	            		success = ((IMQConsumerSimpleCallback)simpleAction).doConsumerBusiness(log.getMsgBody(), record.key().toString());
	            		if (success == null)
	            			success = false;
	            	}else if (this.C_MsgEcho.equals(this.consumerType)){
	            		result = ((IMQConsumerCallback)action).doConsumerBusiness(log.getMsgBody(), record.key().toString());
	            		success = result!=null;
	            		if (success)
	            			callbackBody = JSON.toJSONString(result);
	            	}

            		status = (success != null && success) ? "0" : "1";
	            } catch (MsgStopRetryException e) {
	                ZhphLogger.error("====================消费者停止处理消息！消息主题={},消息ID={},错误信息：{}", record.topic(), record.key(),
	                        e.getMessage());
	                errorMsg = getStackInfo(e);
	                status = "2"; //2为消费不成功停止重试
	            } catch (Throwable e) {
	                ZhphLogger.error("====================消费者处理消息出错！消息主题={},消息ID={},错误信息：{}", record.topic(), record.key(),
	                        e.getMessage() + "," + e.getStackTrace());
	                errorMsg = getStackInfo(e);
	            }

                //保存或更新日志记录
	            try {
	            	consumerLog = this.saveConsumerLog(consumerLog, log, record.topic(), errorMsg, (result == null) ? success : result, status);
				} catch (Exception e) {
					e.printStackTrace();
					ZhphLogger.error("====================消费者处理消息时保存消费日志出错！消息主题={},消息ID={},日志对象：{},错误信息：{}", record.topic(), record.key(),
							(consumerLog!=null?consumerLog.toString():"空对象"), getStackInfo(e));
					continue;
				}
            }else if ("0".equals(consumerLog.getStatus())){
            	success = true;
            }
            
            // 成功返回后，发送成功消息给生产者
            if (success) {
                try {
	            	//手工确认消费消息成功
	            	commitSync(log.getId(), log.getMsgName());
                    messageProducerPool.send(callbackTopics[0], log.getId(), callbackBody);
                } catch (Exception e) {
                    ZhphLogger.error("====================消费者发送确认消息出错！消息主题={},消息ID={},错误信息：{}", log.getCallbackTopicName(), log.getId(),
                            e.getMessage() + "," + e.getStackTrace());
                }
            }else if ("2".equals(consumerLog.getStatus()) || 
            		consumerLog.getRetryCount().intValue() >= consumerLog.getRetryLimit().intValue()){ 
	        	
            	if ("2".equals(consumerLog.getStatus()))
        			callbackBody = "{id:\""+log.getId()+"\",status:\"2\"}";
        		else if (consumerLog.getRetryCount().intValue() >= consumerLog.getRetryLimit().intValue()) //重试次数超过重试限制
        			callbackBody = "{id:\""+log.getId()+"\",retryCount:\""+consumerLog.getRetryCount().intValue()+"\"}";
            	
            	//给生产者发送修改消息状态的消息
            	try {
            		//手工确认消费消息成功
            		commitSync(log.getId(), log.getMsgName());
	                messageProducerPool.send(callbackTopics[callbackTopics.length-1],
	                    		log.getId(), callbackBody);
                } catch (Exception e) {
                    ZhphLogger.error("====================消费者发送确认消息出错！消息主题={},消息ID={},错误信息：{}", log.getCallbackTopicName(), log.getId(),
                            e.getMessage() + "," + e.getStackTrace());
                }
            }
            if (debug)
        		ZhphLogger.debug("========================================当前Consumer为{}，当前线程ID为{}，消息ID为{}，topic为：{},消费消息完成，当前时间：{}",consumer.hashCode(),Thread.currentThread().getId(),
    					record.key().toString(),record.topic(),System.currentTimeMillis());
        }
    }
    
    private String getStackInfo(Throwable a){
    	String stackTrace = StringUtil.getStackTrace(a);
    	if (StringUtil.isEmptyOrNull(stackTrace))
        	stackTrace = "";
    	String msg = a.getMessage()+" "+stackTrace;
        return (msg.length()<=1300)?msg:msg.substring(0, 1300);
    }
    /**
     * 判断是否停止重试：
     * 停止重试的条件：
     * consumerLog !=null && （状态为0或2 || 重试次数大于等于重试最大限制）
     * @param consumerLog
     * @return
     */
    private boolean cancalRetryConsumer(TransMsgConsumerLog consumerLog){
    	return (consumerLog != null &&
    			("0".equals(consumerLog.getStatus()) || "2".equals(consumerLog.getStatus()) || 
    			(consumerLog.getRetryCount()!=null && consumerLog.getRetryLimit()!=null &&
    				consumerLog.getRetryCount().compareTo(consumerLog.getRetryLimit()) >= 0)));
    }
        
    /**
	 * 手工确认消费消息成功
	 * @param topic
	 * @param partition
	 * @param offset
	 */
	private void commitSync(String msgId, String topicName){
		/*TopicPartition topicPartition = new TopicPartition(topic, partition);
		OffsetAndMetadata offsetAndMetadata = new OffsetAndMetadata(offset);
		Map<TopicPartition, OffsetAndMetadata> point = new HashMap<TopicPartition, OffsetAndMetadata>();
		point.put(topicPartition, offsetAndMetadata);*/
		
		try {
    		//手工确认消费消息成功
			consumer.commitSync();
        } catch (Exception e) {
            ZhphLogger.error("====================消费者发送确认消息出错！消息主题={},消息ID={},错误信息：{}", topicName, msgId,
                    e.getMessage() + "," + e.getStackTrace());
        }
	}
	
    /**
     * 保存客户端日志
     * @param consumerLog
     * @param log
     * @param errorMsg
     * @param result
     */
    private TransMsgConsumerLog saveConsumerLog(TransMsgConsumerLog consumerLog, TransMsgLog log, String msgName,
    		String errorMsg, Object result, String status){
    	//保存或更新日志记录
        Date now = new Date();
        if (consumerLog == null){
        	consumerLog = new TransMsgConsumerLog();
        	consumerLog.setCreatedId(log.getCreatedId());
        	consumerLog.setFirstTime(now);
        	consumerLog.setMsgName(msgName);
        	consumerLog.setMsgPublisher(log.getMsgPublisher());
        	consumerLog.setRetryCount(1);
        	consumerLog.setRetryLimit(log.getRetryLimit());
    		consumerLog.setStatus(status);
        }else{
        	consumerLog.setRetryCount(consumerLog.getRetryCount() + 1);
        	consumerLog.setUpdateTime(now);
        	consumerLog.setStatus(status);
        }
        consumerLog.setErrorMsg(errorMsg);
        
       	if ((result instanceof Boolean && (Boolean)result) ||
       			(result instanceof Map && result != null))
       		consumerLog.setConsumerTime(now);
       	
       	if (result instanceof Map && result != null)
       		consumerLog.setCallbackBody(JSON.toJSONString(result));
       	
       	if (StringUtil.isEmptyOrNull(consumerLog.getId())){
       		consumerLog.setId(log.getId());
       		this.transMsgConsumerLogService.add(consumerLog);
       	}else{
       		this.transMsgConsumerLogService.update(consumerLog);
       	}
       	
       	return consumerLog;
    }
    
    /**
     * 消息信息解包成业务消息体和返回消息的Topic
     * @param record 消息记录
     * @return 包含业务消息体和返回消息的Topic的JSONObject
     */
    private TransMsgLog decodeMsgBody(ConsumerRecord<Object, Object> record){
        if (record == null || record.value() == null)
            return null;
        try {
        	String val = record.value().toString();
        	TransMsgLog log = JSON.parseObject(val , TransMsgLog.class);
            return log;
        } catch (Exception e1) {
        	e1.printStackTrace();
            ZhphLogger.error("====================消费者对消息解包出错！消息主题={},消息ID={},错误信息：{}", record.topic(), record.key(),
                    e1.getMessage() + "," + e1.getStackTrace());
            return null;
        }
    }

    /**
     * 消费消息成功后发送确认消息，消息体为消息ID
     *
     * @param records
     *            消息记录
     * @param action
     *            消费消息的业务接口
     * @param params
     *            传递到业务接口内参数
     * @throws RuntimeException
     */
    private void consumerBusinessNonEcho(ConsumerRecords<Object, Object> records, IMQConsumerSimpleCallback action) throws RuntimeException {
        if (records == null || records.count() == 0)
            return;

        for (ConsumerRecord<Object, Object> record : records) {
            // 消费者消费消息
            try {
                action.doConsumerBusiness((String) record.value(), record.key().toString());
                //手工确认消费消息成功
            	commitSync(record.key().toString(), record.topic());
            } catch (Exception e) {
                ZhphLogger.error("====================消费者处理消息出错！消息主题={},消息ID={},错误信息：{}", record.topic(), record.key(),
                        e.getMessage() + "," + e.getStackTrace());
            }
        }
    }
}
