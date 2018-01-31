package com.zhph.common.kafka.service;

import java.util.List;

import com.zhph.common.kafka.model.TransMsgConsumerLog;
import com.zhph.common.kafka.util.ServiceResponse;

/**
 * 描述：消费端日志模块service接口，提供controller操作所需方法
 *
 */
public interface TransMsgConsumerLogService  {

	/**
	 * 新增消费端日志方法
	 * @param transMsgConsumerLog TransMsgConsumerLog:实体类
	 */
	ServiceResponse<TransMsgConsumerLog> add(TransMsgConsumerLog transMsgConsumerLog);
		
	/**
	 * 删除消费端日志方法
	 * @param key:多个由“，”分割开的id字符串
	 */
	ServiceResponse deleteByKey(String key);
	
	/**
	 * 根据主键查找消费端日志实体方法
	 * @param key String：实体主键
	 * @return transMsgConsumerLog TransMsgConsumerLog 实体对象
	 */
	TransMsgConsumerLog getByPrimaryKey(String key);
	
	/**
	 * 根据条件查找消费端日志列表方法
	 * @param transMsgConsumerLog TransMsgConsumerLog 实体对象（查询条件）
	 * @return: 实体对象的list
	 */
	List<TransMsgConsumerLog> listByCondition(TransMsgConsumerLog transMsgConsumerLog);

	/**
	 * 修改消费端日志方法
	 * @param transMsgConsumerLog TransMsgConsumerLog 实体对象
	 */	
	ServiceResponse update(TransMsgConsumerLog transMsgConsumerLog);
	
}