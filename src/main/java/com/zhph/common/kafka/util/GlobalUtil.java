package com.zhph.common.kafka.util;

import java.util.UUID;

public class GlobalUtil {
	/**
	 * 
	 * @return
	 */
	public static String createGlobalId(){
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

//	public static void main(String[] args){
//		System.out.println(GlobalUtil.createGlobalId());
//	}
}
