package com.zhph.common.kafka.exception;

import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.zhph.common.kafka.util.StringUtil;

public class ServiceException extends java.lang.RuntimeException {
    
    
	private String errCode;//系统错误代码
	private String message;
	
	public static final Map<String, String> MYSQL_ERR_CODE_MSG = new TreeMap<String, String>();
	
	static {
		MYSQL_ERR_CODE_MSG.put("1043", "无效连接。");
		MYSQL_ERR_CODE_MSG.put("1042", "无效的主机名。");
		MYSQL_ERR_CODE_MSG.put("1040", "已到达数据库的最大连接数。");
	}
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public ServiceException(String message) {		
		super(new Exception(message));
		this.message = message;
	}
	
	public ServiceException(Throwable t){
		super(t);
		this.errCode=CodeConst.SERVICE_ERR_CODE_DEFAULT;
		message=t.getMessage();
		if (StringUtil.isEmptyOrNull(message)){
//			message=ErrorCodeDefine.getMessage(errCode,null);
		}
		if(t instanceof java.sql.SQLException){
			message = transIfxErrMsg(message);
		}
	}

	public static String transIfxErrMsg(String err) {
		
		if (isChinase(err)) {
			return err;
		}
		String errKey = "";
		if (err.indexOf("[1043]") > 0) {
			errKey = "1043";
		} else if (err.indexOf("[1042") > 0) {
			errKey = "1042";
		} else if (err.indexOf("[1040]") > 0) {
			errKey = "1040";
		} 
		if(StringUtil.isEmptyOrNull(errKey)){
			return err;
		}
		return MYSQL_ERR_CODE_MSG.get(errKey);
	}
	
	public ServiceException(String errCode,Throwable t){
		super(t);
		this.errCode=errCode;	
		if(!StringUtil.isEmptyOrNull(errCode)&&isChinase(errCode)){
			message=errCode;
		}else{
//			message=ErrorCodeDefine.getMessage(errCode,null);
		}
		if (StringUtil.isEmptyOrNull(message)){
			message=t.getMessage();
		}
	}
	
	public static  boolean isChinase(String str){
		Pattern pattern=Pattern.compile("[\u4e00-\u9fa5]");   
		Matcher matcher=pattern.matcher(str); 
		return matcher.find();
	}
	
	public ServiceException(String errCode,Throwable t,String[] fixMsgs){
		super(t);
		this.errCode=errCode;	
//		message=ErrorCodeDefine.getMessage(errCode,fixMsgs);
	}
	
	public void setFixMsgs(String[] fixMsgs){
//		message=ErrorCodeDefine.getMessage(errCode,fixMsgs);
	}
	
	public String getErrCode(){
		return errCode;
	}
}

