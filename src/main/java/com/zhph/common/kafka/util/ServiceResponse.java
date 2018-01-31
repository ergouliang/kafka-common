/**    
 * @Title: ServiceResponse.java
 * @Package com.cms.model.util
 * @Description: TODO
 * @author 母德亮
 * @date 2016年8月2日 下午12:04:04
 * @version V1.0
 */
package com.zhph.common.kafka.util;

import java.io.Serializable;

/**
 * @ClassName: ServiceResponse
 * @Description: Service响应对象
 */
public class ServiceResponse<T> implements Serializable {
	/** @Field serialVersionUID  */
	private static final long serialVersionUID = 1L;
	/** @Field success 成功状态 */
	private boolean success;
	/** @Field code 状态码 */
	private String code;
	/** @Field msg 消息 */
	private String msg;
	/** @Field data 数据 */
	private T data;
		
	public boolean getSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	
	
}
