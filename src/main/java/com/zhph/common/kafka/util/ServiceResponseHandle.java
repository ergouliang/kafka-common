/**    
 * @Title: ServiceResponse.java
 * @Package com.cms.model.util
 * @Description: TODO
 * @author 母德亮
 * @date 2016年8月2日 下午12:04:04
 * @version V1.0
 */
package com.zhph.common.kafka.util;

/**
 * @ClassName: ServiceResponse
 * @Description: Service响应对象
 */
public class ServiceResponseHandle<T> {
	
	/**
	 * 成功
	 * @param data
	 * @return ServiceResponse
	 */
	public static <T> ServiceResponse<T> success(T data) {
		ServiceResponse<T> wrs = new ServiceResponse<T>();
		wrs.setSuccess(true);
		wrs.setData(data);
		return wrs;
	}
	/**
	 * 失败
	 * @param msg 失败消息
	 * @return ServiceResponse
	 */
	public static ServiceResponse failed(String msg) {
		ServiceResponse wrs = new ServiceResponse();
		wrs.setMsg(msg);
		wrs.setSuccess(false);
		return wrs;
	}
	/**
	 * 失败
	 * @param code 失败码
	 * @param msg 失败消息
	 * @return ServiceResponse
	 */
	public static ServiceResponse failed(String code, String msg) {
		ServiceResponse wrs = failed(msg);
		wrs.setCode(code);
		return wrs;
	}
		
}
