package com.zhph.common.kafka.service;

import java.util.List;
import java.util.Map;

import com.zhph.common.kafka.model.TransMsgLog;

public interface TransMsgLogService {
    /**
     * 保存日志信息
     * @param log 日志对象
     * @throws RuntimeException
     */
	void saveMsgLog(TransMsgLog log) throws RuntimeException;
	/**
	 * 保存HTTP接口调用日志
	 * @param msgName 接口名称
	 * @param isSuccess 是否调用成功
	 * @param id 消息ID
	 * @param params 接口调用参数
	 * @param errorInfo 接口调用后的返回消息
	 * @throws RuntimeException
	 */
	void saveHttpMsgLog(String msgName, boolean isSuccess, String id, 
			Map<String,Object> params, String errorInfo) throws RuntimeException;
	/**
     * 更新日志状态为已消费
     * @param msgLogId 日志ID
     * @throws RuntimeException
     */
	void updateMsgLogStatus(String msgLogId) throws RuntimeException;
	/**
	 * 查询没有消费成功的消息记录
	 * @param msgType 消息类型 0消息队列  1http
	 * @return 消息日志列表
	 * @throws RuntimeException
	 */
	List<TransMsgLog> selectMsgOfNeedSend(String msgType) throws RuntimeException;
	/**
     * 更新日志信息
     * @param log 日志对象
     * @throws RuntimeException
     */
    void updateMsgLog(final TransMsgLog log) throws RuntimeException;
    
    /**
     * 批量更新日志信息
     * @param logs
     * @throws RuntimeException
     */
    void updateMsgLogOfBatch(final List<TransMsgLog> logs) throws RuntimeException;
    
    /**
     * 根据ID查找记录
     * @param id
     * @return
     */
    TransMsgLog selectByPrimaryKey(String id);
    
    /**
     * 获取所有未回执的topics
     * @return
     */
    List<String> selectAllUnRetryTopics();
}
