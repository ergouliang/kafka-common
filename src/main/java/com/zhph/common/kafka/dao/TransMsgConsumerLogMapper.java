package com.zhph.common.kafka.dao;

import java.util.List;

import com.zhph.common.kafka.model.TransMsgConsumerLog;

/**
 * 描述：消费端日志模块dao接口，提供数据库操作方法
 *
 */
public interface TransMsgConsumerLogMapper  {

	/**
	 * 新增消费端日志方法
	 * @param transMsgConsumerLog TransMsgConsumerLog:实体类
	 */
	void add(TransMsgConsumerLog transMsgConsumerLog);
	
	/**
	 * 新增消费端日志方法，过滤为空的属性
	 * @param transMsgConsumerLog TransMsgConsumerLog:实体类
	 */
	void addSelective(TransMsgConsumerLog transMsgConsumerLog);
	
	/**
	 * 删除消费端日志方法
	 * @param key String:多个由“，”分割开的id字符串
	 */
	void deleteByKey(String key);
	
	/**
	 * 根据主键查找消费端日志实体方法
	 * @param key String：实体主键（查询条件）
	 * @return TransMsgConsumerLog: 实体
	 */
	TransMsgConsumerLog getByPrimaryKey(String key);
	
	/**
	 * 根据条件查找消费端日志列表方法
	 * @param TransMsgConsumerLog transMsgConsumerLog：实体对象（查询条件）
	 * @return List<TransMsgConsumerLog>: 实体对象的list
	 */
	List<TransMsgConsumerLog>  listByCondition(TransMsgConsumerLog transMsgConsumerLog);
	
	/**
	 * 修改消费端日志方法
	 * @param transMsgConsumerLog TransMsgConsumerLog：实体对象
	 */	
	void update(TransMsgConsumerLog transMsgConsumerLog);
	
	/**
	 * 修改消费端日志方法，过滤为空的属性
	 * @param transMsgConsumerLog TransMsgConsumerLog：实体对象
	 */	
	void updateSelective(TransMsgConsumerLog transMsgConsumerLog);
}