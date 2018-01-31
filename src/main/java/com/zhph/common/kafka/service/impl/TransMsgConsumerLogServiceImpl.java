package com.zhph.common.kafka.service.impl;
import java.util.List;

import javax.annotation.Resource;

import com.zhph.common.kafka.dao.TransMsgConsumerLogMapper;
import com.zhph.common.kafka.model.TransMsgConsumerLog;
import com.zhph.common.kafka.service.TransMsgConsumerLogService;
import com.zhph.common.kafka.util.GlobalUtil;
import com.zhph.common.kafka.util.ServiceResponse;
import com.zhph.common.kafka.util.ServiceResponseHandle;
import com.zhph.common.kafka.util.StringUtil;

/**
 * 描述：消费端日志模块service接口实现类，实现service接口方法
 *
 */
public class TransMsgConsumerLogServiceImpl implements TransMsgConsumerLogService {
    @Resource
	private TransMsgConsumerLogMapper transMsgConsumerLogMapper;
	
 
	/**
	 * 新增消费端日志方法
	 * @param transMsgConsumerLog:实体类
	 */	
	@Override
	public ServiceResponse<TransMsgConsumerLog> add(TransMsgConsumerLog transMsgConsumerLog){
	    if (StringUtil.isEmptyOrNull(transMsgConsumerLog.getId()))
            transMsgConsumerLog.setId(GlobalUtil.createGlobalId());
		transMsgConsumerLogMapper.addSelective(transMsgConsumerLog);
		return ServiceResponseHandle.success(transMsgConsumerLog);
	}
		
	/**
	 * 根据主键查找消费端日志实体方法
	 * @param key String 实体主键
	 * @return TransMsgConsumerLog 实体对象
	 */
	@Override
	public TransMsgConsumerLog getByPrimaryKey(String key){
		return transMsgConsumerLogMapper.getByPrimaryKey(key);
	}
	
	/**
	 * 删除消费端日志方法
	 * @param key String 多个由“，”分割开的id字符串
	 */
	@Override
	public ServiceResponse deleteByKey(String key){
		transMsgConsumerLogMapper.deleteByKey(key);
		return ServiceResponseHandle.success(null);
	}
	
	/**
	 * 根据条件查找消费端日志列表方法
	 * @param transMsgConsumerLog TransMsgConsumerLog 实体对象（查询条件）
	 * @return List<TransMsgConsumerLog> 实体对象的list
	 */
	@Override
	public List<TransMsgConsumerLog> listByCondition(TransMsgConsumerLog transMsgConsumerLog){
		return transMsgConsumerLogMapper.listByCondition(transMsgConsumerLog);
	}
	
	/**
	 * 修改消费端日志方法
	 * @param transMsgConsumerLog TransMsgConsumerLog 实体对象
	 */	
	@Override
	public ServiceResponse update(TransMsgConsumerLog transMsgConsumerLog){
		transMsgConsumerLogMapper.updateSelective(transMsgConsumerLog);
		return ServiceResponseHandle.success(null);
	}
}