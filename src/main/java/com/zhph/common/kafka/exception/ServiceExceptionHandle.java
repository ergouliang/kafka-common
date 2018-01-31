package com.zhph.common.kafka.exception;

import java.util.Arrays;

import com.zhph.common.kafka.util.log4j.ZhphLogger;
import org.aspectj.lang.JoinPoint;

/**
 * Service层方法异常处理类. 处理的手段主要有以下两个步骤：
 *  1、记录日志； 
 *  2、将异常封装成ServiceResponse对象作为方法的返回值返回
 *
 */
public class ServiceExceptionHandle {
	
	/**
	 * afterThrowing拦截异常处理方法
	 * @param point 代理
	 * @param e 异常
	 * @return 错误信息包装对象ServiceResponse
	 */
	public void exceptionHandleProcess(JoinPoint point, Exception e) throws Exception {
		if (e != null){
			this.exceptionLog(point, e.getMessage()+" \n "+e.getStackTrace());
			throw e;
		}
	}
	
	/**
	 * 记录异常日志
	 * @param point 代理
	 * @param message 异常信息
	 */
	private void exceptionLog(JoinPoint point, String message){
		String methodName = point.getSignature().getDeclaringTypeName() +
				"." + point.getSignature().getName();
		String args = Arrays.toString(point.getArgs());
		ZhphLogger.error("调用{}方法出错，参数列表为{}，错误信息为{}", methodName, args, message);
	}
}
