package com.zhph.common.kafka.model;

import java.util.Date;
/**
 * 描述：日志模块实体类，负责页面与后台数据传输功能
 *
 */
public class TransMsgLog implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;

	private String id;

	/**
	 * 消息业务ID
	 **/
	private String msgPublisher;
	/**
	 * 发布时间
	 **/
	private Date publishTime;
	/**
	 * 消息名称
	 **/
	private String msgName;
	/**
	 * 消息体（Json字符串）
	 **/
	private String msgBody;
	/**
	 * 消息状态：0删除 1待发送 2回滚成功 3回滚失败
	 **/
	private String status;
	/**
	 * 重试次数
	 **/
	private Integer retryCount;
	/**
	 * 重试最大次数限制
	 **/
	private Integer retryLimit;
	/**
	 * 错误消息
	 **/
	private String errorMsg;
	/**
	 * 更新时间
	 **/
	private Date updateTime;
	/**
	 * 消息类型: 0消息队列 1http 2dubbo
	 **/
	private String msgType;
	/**
	 * 成功消费时间
	 **/
	private Date consumerTime;
	/**
	 * 
	 **/
	private String msgBody1;
	/**
	 * 
	 **/
	private String msgBody2;
	/**
	 * 
	 **/
	private String msgBody3;
	/**
	 * 
	 **/
	private String msgBody4;
	/**
	 * 
	 **/
	private String msgBody5;
	/**
	 * 创建用户ID
	 **/
	private String createdId;

	private String callbackTopicName;
	
	/**
	 * 分区
	 */
	private Integer partition;
	
	/**
	 * 被分区编号，希望进入同一分区的业务号
	 */
	private String partitionNo;

	public Integer getPartition() {
		return partition;
	}

	public void setPartition(Integer partition) {
		this.partition = partition;
	}
	
	public String getPartitionNo() {
		return partitionNo;
	}

	public void setPartitionNo(String partitionNo) {
		this.partitionNo = partitionNo;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMsgPublisher() {
		return msgPublisher;
	}

	public void setMsgPublisher(String msgPublisher) {
		this.msgPublisher = msgPublisher;
	}

	public Date getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(Date publishTime) {
		this.publishTime = publishTime;
	}

	public String getMsgName() {
		return msgName;
	}

	public void setMsgName(String msgName) {
		this.msgName = msgName;
	}

	public String getMsgBody() {
		return msgBody;
	}

	public void setMsgBody(String msgBody) {
		this.msgBody = msgBody;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(Integer retryCount) {
		this.retryCount = retryCount;
	}

	public Integer getRetryLimit() {
		return retryLimit;
	}

	public void setRetryLimit(Integer retryLimit) {
		this.retryLimit = retryLimit;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public Date getConsumerTime() {
		return consumerTime;
	}

	public void setConsumerTime(Date consumerTime) {
		this.consumerTime = consumerTime;
	}

	public String getMsgBody1() {
		return msgBody1;
	}

	public void setMsgBody1(String msgBody1) {
		this.msgBody1 = msgBody1;
	}

	public String getMsgBody2() {
		return msgBody2;
	}

	public void setMsgBody2(String msgBody2) {
		this.msgBody2 = msgBody2;
	}

	public String getMsgBody3() {
		return msgBody3;
	}

	public void setMsgBody3(String msgBody3) {
		this.msgBody3 = msgBody3;
	}

	public String getMsgBody4() {
		return msgBody4;
	}

	public void setMsgBody4(String msgBody4) {
		this.msgBody4 = msgBody4;
	}

	public String getCreatedId() {
		return createdId;
	}

	public void setCreatedId(String createdId) {
		this.createdId = createdId;
	}

	public String getCallbackTopicName() {
		return callbackTopicName;
	}

	public void setCallbackTopicName(String callbackTopicName) {
		this.callbackTopicName = callbackTopicName;
	}

	public String getMsgBody5() {
		return msgBody5;
	}

	public void setMsgBody5(String msgBody5) {
		this.msgBody5 = msgBody5;
	}

}