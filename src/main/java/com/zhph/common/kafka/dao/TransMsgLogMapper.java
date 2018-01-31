package com.zhph.common.kafka.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zhph.common.kafka.model.TransMsgLog;

public interface TransMsgLogMapper {
    int deleteByPrimaryKey(String id);

    int insert(TransMsgLog record);

    int insertSelective(TransMsgLog record);

    TransMsgLog selectByPrimaryKey(String id);
    
    List<TransMsgLog> selectMsgOfNeedSend(@Param("msgType") String msgType);

    int updateByPrimaryKeySelective(TransMsgLog record);

    int updateByPrimaryKey(TransMsgLog record);
}