package com.zhph.common.kafka.service.impl;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.zhph.common.kafka.dao.TransMsgLogMapper;
import com.zhph.common.kafka.model.TransMsgLog;
import com.zhph.common.kafka.service.TransMsgLogService;
import com.zhph.common.kafka.util.StringUtil;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Service;

/**
 * 日志服务实现类
 *
 */
@Service
public class TransMsgLogServiceImpl implements TransMsgLogService {

    @Autowired
    private TransMsgLogMapper logMapper;
    
    /**
     * 保存日志信息
     * @param log 日志对象
     * @throws RuntimeException
     */
    @Override
    public void saveMsgLog(final TransMsgLog log) throws RuntimeException {
        if (log == null || StringUtil.isEmptyOrNull(log.getId())
        		|| StringUtil.isEmptyOrNull(log.getMsgBody())
        		|| StringUtil.isEmptyOrNull(log.getMsgName()))
            throw new RuntimeException("需要保存的日志为空或ID为空！");
        splitMsgBody(log);
        logMapper.insertSelective(log);
    }
    /**
     * 更新日志信息
     * @param log 日志对象
     * @throws RuntimeException
     */
    @Override
    public void updateMsgLog(final TransMsgLog log) throws RuntimeException {
        if (log == null || log.getId() == null || "".equals(log.getId()))
            throw new RuntimeException("需要保存的日志为空！");
        if (StringUtil.isEmptyOrNull(log.getMsgBody()))
        	splitMsgBody(log);
        logMapper.updateByPrimaryKeySelective(log);
    }
    
    /**
     * 拆解MsgBody
     * @param log
     */
    private void splitMsgBody(final TransMsgLog log){
    	if (log == null || StringUtil.isEmptyOrNull(log.getMsgBody()))
    		return;
		try {
			byte[] msgBodyBase64 = Base64.encodeBase64(log.getMsgBody().getBytes("UTF-8"));
			if (msgBodyBase64.length<=4000)
				log.setMsgBody1(new String(msgBodyBase64,"UTF-8"));
			else{
				log.setMsgBody1(new String(Arrays.copyOf(msgBodyBase64, 4000),"UTF-8"));
				log.setMsgBody2(new String(Arrays.copyOfRange(msgBodyBase64, 4000,(msgBodyBase64.length<=7990 ? msgBodyBase64.length : 7990)),"UTF-8"));
				if (msgBodyBase64.length > 7990)
					log.setMsgBody3(new String(Arrays.copyOfRange(msgBodyBase64, 7990,(msgBodyBase64.length<=11981 ? msgBodyBase64.length : 11981)),"UTF-8"));
				if (msgBodyBase64.length > 11981)
					log.setMsgBody4(new String(Arrays.copyOfRange(msgBodyBase64, 11981,(msgBodyBase64.length<=15971 ? msgBodyBase64.length : 15971)),"UTF-8"));
				if (msgBodyBase64.length > 15971)
					log.setMsgBody5(new String(Arrays.copyOfRange(msgBodyBase64, 15971,(msgBodyBase64.length<=19961 ? msgBodyBase64.length : 19961)),"UTF-8"));
			}
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("转换成UTF-8时出错",e);
		}
    }
    /**
     * 更新日志状态为已消费
     * @param msgLogId 日志ID
     * @throws RuntimeException
     */
    @Override
    public void updateMsgLogStatus(final String msgLogId) throws RuntimeException {
        try {
            TransMsgLog log = new TransMsgLog();
            log.setId(msgLogId);
            log.setStatus("0");
            log.setConsumerTime(new Date());
            logMapper.updateByPrimaryKeySelective(log);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * 查询没有消费成功的消息记录
     * @param msgType 消息类型 0消息队列  1http
     * @return 消息日志列表
     * @throws RuntimeException
     */
    @Override
    public List<TransMsgLog> selectMsgOfNeedSend(final String msgType) throws RuntimeException {
    	List<TransMsgLog> logs = logMapper.selectMsgOfNeedSend(msgType);
    	if (logs == null || logs.size() == 0) return null;
    	
    	try {
			for (TransMsgLog log : logs) {
				if (! StringUtil.isEmptyOrNull(log.getMsgBody2())){
					byte[] msg14 = Arrays.concatenate(log.getMsgBody1().getBytes("UTF-8"),
							log.getMsgBody2().getBytes("UTF-8"),
							(log.getMsgBody3() != null)?log.getMsgBody3().getBytes("UTF-8"):null,
							(log.getMsgBody4() != null)?log.getMsgBody4().getBytes("UTF-8"):null);
					byte[] msg15 = null;
					if (! StringUtil.isEmptyOrNull(log.getMsgBody5())){
						msg15 = Arrays.concatenate(msg14, log.getMsgBody5().getBytes("UTF-8"));
						log.setMsgBody(new String(Base64.decodeBase64(msg15),"UTF-8"));
					}else
						log.setMsgBody(new String(Base64.decodeBase64(msg14),"UTF-8"));
				}else
					log.setMsgBody(new String(Base64.decodeBase64(log.getMsgBody1().getBytes("UTF-8")),"UTF-8"));
			}
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("转换成UTF-8时出错",e);
		}
    	
    	return logs;
    }
    
    /**
     * 保存HTTP接口调用日志
     * @param msgName 接口名称
     * @param isSuccess 是否调用成功
     * @param id 消息ID
     * @param params 接口调用参数
     * @param errorInfo 接口调用后的返回消息
     * @throws RuntimeException
     */
    @Override
    public void saveHttpMsgLog(final String msgName, final boolean isSuccess, final String id,
            final Map<String, Object> params, final String errorInfo) throws RuntimeException {
        TransMsgLog log = new TransMsgLog();
        log.setMsgName(msgName);
        log.setMsgPublisher(id);
        log.setMsgType("1");
        log.setPublishTime(new Date());
        if (isSuccess)
            log.setStatus("0");
        else {
            log.setStatus("1");
            if (errorInfo != null && !"".equals(errorInfo))
                params.put("errorInfo", errorInfo);
        }
        log.setMsgBody1(JSON.toJSONString(params));
//        log.setId(GlobalUtil.createGlobalId());

        this.saveMsgLog(log);
    }
    
	@Override
	public TransMsgLog selectByPrimaryKey(String id) {
		return logMapper.selectByPrimaryKey(id);
	}
}
