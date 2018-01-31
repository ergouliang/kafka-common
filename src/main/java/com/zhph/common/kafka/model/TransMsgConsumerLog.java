package com.zhph.common.kafka.model;

import java.util.Date;

/**
 * 描述：消费端日志模块实体类，负责页面与后台数据传输功能
 *
 */
public class TransMsgConsumerLog {

	private String id;
	/**
	 * 消息业务ID
	 **/
	private String msgPublisher;
	/**
	 * 第一次消费时间
	 **/
	private Date firstTime;
	/**
	 * 消息名称
	 **/
	private String msgName;
	/**
	 * 消息状态：0消费成功 1消费失败
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
	 * 成功消费时间
	 **/
	private Date consumerTime;
	/**
	 * 创建用户ID
	 **/
	private String createdId;
	/**
	 * 返回的消息体（用于自定义返回消息）
	 */
	private String callbackBody;

	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 获取 消息业务ID
	 * 
	 * @return String this.msgPublisher
	 */
	public String getMsgPublisher() {
		return this.msgPublisher;
	}

	/**
	 * 设置 消息业务ID
	 * 
	 * @param String
	 *            msgPublisher
	 */
	public void setMsgPublisher(String msgPublisher) {
		this.msgPublisher = msgPublisher;
	}

	/**
	 * 获取 第一次消费时间
	 * 
	 * @return Date this.firstTime
	 */
	public Date getFirstTime() {
		return this.firstTime;
	}

	/**
	 * 设置 第一次消费时间
	 * 
	 * @param Date
	 *            firstTime
	 */
	public void setFirstTime(Date firstTime) {
		this.firstTime = firstTime;
	}

	/**
	 * 获取 消息名称
	 * 
	 * @return String this.msgName
	 */
	public String getMsgName() {
		return this.msgName;
	}

	/**
	 * 设置 消息名称
	 * 
	 * @param String
	 *            msgName
	 */
	public void setMsgName(String msgName) {
		this.msgName = msgName;
	}

	/**
	 * 获取 消息状态：0消费成功 1消费失败
	 * 
	 * @return String this.status
	 */
	public String getStatus() {
		return this.status;
	}

	/**
	 * 设置 消息状态：0消费成功 1消费失败
	 * 
	 * @param String
	 *            status
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * 获取 重试次数
	 * 
	 * @return Integer this.retryCount
	 */
	public Integer getRetryCount() {
		return this.retryCount;
	}

	/**
	 * 设置 重试次数
	 * 
	 * @param Integer
	 *            retryCount
	 */
	public void setRetryCount(Integer retryCount) {
		this.retryCount = retryCount;
	}

	/**
	 * 获取 重试最大次数限制
	 * 
	 * @return Integer this.retryLimit
	 */
	public Integer getRetryLimit() {
		return this.retryLimit;
	}

	/**
	 * 设置 重试最大次数限制
	 * 
	 * @param Integer
	 *            retryLimit
	 */
	public void setRetryLimit(Integer retryLimit) {
		this.retryLimit = retryLimit;
	}

	/**
	 * 获取 错误消息
	 * 
	 * @return String this.errorMsg
	 */
	public String getErrorMsg() {
		return this.errorMsg;
	}

	/**
	 * 设置 错误消息
	 * 
	 * @param String
	 *            errorMsg
	 */
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	/**
	 * 获取 更新时间
	 * 
	 * @return Date this.updateTime
	 */
	public Date getUpdateTime() {
		return this.updateTime;
	}

	/**
	 * 设置 更新时间
	 * 
	 * @param Date
	 *            updateTime
	 */
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	/**
	 * 获取 成功消费时间
	 * 
	 * @return Date this.consumerTime
	 */
	public Date getConsumerTime() {
		return this.consumerTime;
	}

	/**
	 * 设置 成功消费时间
	 * 
	 * @param Date
	 *            consumerTime
	 */
	public void setConsumerTime(Date consumerTime) {
		this.consumerTime = consumerTime;
	}

	/**
	 * 获取 创建用户ID
	 * 
	 * @return String this.createdId
	 */
	public String getCreatedId() {
		return this.createdId;
	}

	/**
	 * 设置 创建用户ID
	 * 
	 * @param String
	 *            createdId
	 */
	public void setCreatedId(String createdId) {
		this.createdId = createdId;
	}

	public String getCallbackBody() {
		return callbackBody;
	}

	public void setCallbackBody(String callbackBody) {
		this.callbackBody = callbackBody;
	}

	@Override
	public String toString() {
		return "TransMsgConsumerLog{" + "id='" + id + '\'' + '\'' + ", msgPublisher='" + msgPublisher + '\''
				+ ", firstTime='" + firstTime + '\'' + ", msgName='" + msgName + '\'' + ", status='" + status + '\''
				+ ", retryCount='" + retryCount + '\'' + ", retryLimit='" + retryLimit + '\'' + ", errorMsg='"
				+ errorMsg + '\'' + ", updateTime='" + updateTime + '\'' + ", consumerTime='" + consumerTime + '\''
				+ ", createdId='" + createdId + '\'' + '}';
	}
}